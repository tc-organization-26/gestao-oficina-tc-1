# RFC-0005 — Observabilidade integrada com Datadog

- **Data:** 02/10/2026
- **Status:** Encerrada – Aprovada

## Resumo

Centralizar métricas, logs e traces da Oficina no Datadog para correlacionar falhas, latência, consumo de recursos e eventos operacionais da aplicação.

## Problema

O fluxo de uma requisição atravessa Gateway, autenticação, Kubernetes, API e banco. Logs isolados dificultam relacionar erros ao consumo de recursos e ao comportamento dos endpoints. Também é necessário acompanhar o volume de ordens de serviço e o tempo de permanência nos status para entender o atendimento.

## Proposta técnica

Instalar Datadog Operator, Agent e Cluster Agent no EKS. Instrumentar o Java pelo tracer nativo via Single Step Instrumentation e coletar logs JSON, métricas Kubernetes e traces HTTP/JDBC. Usar tags de serviço, ambiente e versão, além de `request_id` e IDs de trace quando presentes, para correlacionar os sinais.

Versionar dashboards, pipeline de logs, métricas e monitores em `observability/datadog`, aplicados por Terraform na esteira da API. Coletar eventos como `os.created`, `os.status_duration`, `os.processing_failed` e `integration.failed`. Habilitar as layers Datadog das Lambdas quando a configuração compartilhada estiver disponível; os logs Lambda também seguem para CloudWatch. O monitoramento externo descrito no guia utiliza UptimeRobot.

## Impacto esperado

A correlação facilita investigar latência e falhas e distinguir saúde do processo de falhas técnicas dos casos de uso. O valor depende da chegada dos dados e da configuração de filtros, retenção e alertas; a existência dos arquivos não comprova coleta em um ambiente ativo.

Há custo de infraestrutura dos Agents e de contratação/consumo do SaaS, variável com plano, volume, retenção, APM e cardinalidade. Não há orçamento ou cotação registrados aqui. Os workloads privados precisam de saída para o serviço. A esteira depende do Operator e dos Agents antes do rollout; falhas no Terraform Datadog após o rollout podem deixar a aplicação atualizada com configuração de observabilidade incompleta.

Os logs de negócio são enviados em best effort, sem outbox nem garantia de entrega exatamente uma vez. O histórico SQL permanece a referência para auditoria do tempo por status. A instrumentação não cria envio real de notificações por e-mail nos listeners de negócio que atualmente apenas registram logs.

## Alternativas consideradas

Como comparação retrospectiva, CloudWatch como ferramenta principal reduziria a quantidade de fornecedores, mas exigiria redesenhar dashboards, correlação e instrumentação para o objetivo atual. Prometheus, Grafana e ferramentas de logs e traces separadas permitiriam compor outra solução, com mais responsabilidades de integração e operação. OpenTelemetry poderia reduzir dependência da instrumentação específica, exigindo adaptação e validação. Não foram registrados testes comparativos ou cotações dessas alternativas.

## Pontos em aberto

- Medir ingestão e custo, definir retenção e controlar cardinalidade.
- Ajustar alertas com comportamento observado e validar seus destinos de notificação.
- Revisar os dados enviados nos logs e validar coleta/correlação nos dois ambientes.

Registro retrospectivo individual, conforme a [convenção dos documentos](../README.md). Decisão no [ADR-0010](../adrs/0010-adotar-datadog.md). Evidências: [guia de observabilidade](../../OBSERVABILIDADE.md), [Agent](../../../k8s/observability/datadog-agent.yaml) e [recursos Terraform](../../../observability/datadog/main.tf).
