#!/usr/bin/env bash
set -euo pipefail

: "${DEPLOY_ENV:?}"
: "${AWS_REGION:?}"
: "${DD_API_KEY:?}"
: "${DD_SITE:?}"
: "${DD_OPERATOR_CHART_VERSION:?}"

cluster_name="$(aws ssm get-parameter --name "/oficina/${DEPLOY_ENV}/eks/cluster-name" --query 'Parameter.Value' --output text)"
helm repo add datadog https://helm.datadoghq.com
helm repo update datadog
helm upgrade --install datadog-operator datadog/datadog-operator \
  --version "$DD_OPERATOR_CHART_VERSION" --namespace datadog --create-namespace \
  --wait --timeout 5m
kubectl wait --for=condition=Established crd/datadogagents.datadoghq.com --timeout=120s

# Send the key through stdin; never print the Secret manifest.
jq -n --arg key "$DD_API_KEY" \
  '{apiVersion:"v1",kind:"Secret",metadata:{name:"datadog-secret",namespace:"datadog"},type:"Opaque",stringData:{"api-key":$key}}' \
  | kubectl apply -f -

patch="$(jq -nc --arg cluster "$cluster_name" --arg site "$DD_SITE" --arg env "$DEPLOY_ENV" \
  '{spec:{global:{clusterName:$cluster,site:$site,tags:[("env:"+$env),"project:oficina"]}}}')"
kubectl patch --local -f k8s/observability/datadog-agent.yaml --type merge \
  -p "$patch" -o yaml | kubectl apply -f -

# The Operator creates these workloads asynchronously after accepting the CR.
for resource in deployment/datadog-cluster-agent daemonset/datadog-agent; do
  found=false
  for ((attempt=0; attempt<60; attempt++)); do
    if kubectl -n datadog get "$resource" >/dev/null 2>&1; then
      found=true
      break
    fi
    sleep 5
  done
  if [[ "$found" != true ]]; then
    echo "::error::O Operator nao criou $resource."
    kubectl -n datadog get datadogagent datadog -o yaml
    exit 1
  fi
  kubectl -n datadog rollout status "$resource" --timeout=300s
done
