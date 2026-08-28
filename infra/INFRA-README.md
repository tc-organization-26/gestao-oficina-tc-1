# Infraestrutura com Terraform e Kubernetes

Este diretório provisiona os recursos Kubernetes da Oficina API usando Terraform.

O Terraform cria recursos individuais pelo provider Kubernetes.

Para instruções de execução, consulte [`../docs/EXECUCAO.md`](../docs/EXECUCAO.md). Para problemas comuns, consulte [`../docs/TROUBLESHOOTING.md`](../docs/TROUBLESHOOTING.md).

## Recursos criados

- Namespace `oficina`.
- ConfigMap e Secret do PostgreSQL.
- PVC `postgres-data` para persistência dos dados do PostgreSQL.
- Deployment e Service interno do PostgreSQL.
- ConfigMap e Secret da API.
- Secret opcional `ghcr-credentials` para pull de imagem privada no GHCR.
- Deployment e Service `NodePort` da API.
- HPA da API por CPU e memória.

## Arquivos Terraform

- `providers.tf`: configura a versão do Terraform, o provider Kubernetes e o backend Kubernetes.
- `variables.tf`: declara variáveis da infraestrutura, incluindo valores sensíveis.
- `main.tf`: cria os recursos Kubernetes.
- `outputs.tf`: expõe informações úteis após o `terraform apply`.
- `terraform.tfvars.example`: exemplo para criar o arquivo local `terraform.tfvars`.

O arquivo `terraform.tfvars` não deve ser versionado, pois pode conter senha do banco e segredo JWT.

## Desenho da infraestrutura

```text
Namespace oficina
  |
  |-- Deployment oficina-api
  |     |-- ConfigMap oficina-api-config
  |     |-- Secret oficina-api-secret
  |     |-- Secret ghcr-credentials, quando configurado
  |     |-- Service oficina-api, NodePort 30081
  |     |-- HPA oficina-api
  |
  |-- Deployment postgres
        |-- ConfigMap postgres-config
        |-- Secret postgres-secret
        |-- PVC postgres-data
        |-- Service postgres, ClusterIP
```

## Backend do Terraform

O projeto usa backend Kubernetes para armazenar o state em um Secret no namespace `default` do cluster.

Na execução local:

```powershell
terraform init -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

No Continuous Deployment, o workflow usa o kubeconfig recebido pelo secret `KUBE_CONFIG_BASE64` e inicializa o backend com `secret_suffix=oficina-api`.

## Relação com os manifestos YAML

Os recursos principais da API e do banco são gerenciados pelo Terraform.

Os manifestos em `k8s/cd` são complementares e aplicados pelo GitHub Actions após o `terraform apply`. Isso evita duplicar via `kubectl apply` os recursos que já são controlados pelo Terraform.

## Observações

- O banco roda dentro do Kubernetes usando PostgreSQL 16 Alpine.
- Os dados do banco ficam no PVC `postgres-data`.
- ConfigMaps guardam configurações não sensíveis.
- Secrets guardam usuários, senhas, tokens e credenciais de registry.
- O HPA depende do Metrics Server instalado no cluster.

