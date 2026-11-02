# ADR-0010 — Adotar Datadog para observabilidade

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

A investigação de falhas exige relacionar requisições, banco, recursos Kubernetes e eventos do fluxo de OS. Logs isolados não fornecem essa visão. O projeto individual implementa Datadog e mantém sua configuração em código. Este registro retrospectivo não afirma que tenha ocorrido discussão em equipe, comparação experimental ou aprovação externa. A [RFC-0005](../rfcs/0005-observabilidade-com-datadog.md) apresenta a proposta e as alternativas técnicas.

## Decisão

Centralizamos métricas, logs e traces no Datadog. Usamos Operator, Agent e Cluster Agent no EKS, tracer Java por Single Step Instrumentation, logs JSON e tags de serviço, ambiente e versão. Correlacionamos requisições por `request_id` e, quando o tracer está ativo, IDs de trace/span.

Versionamos dashboards, pipeline, métricas e monitores por Terraform no repositório da API. Acompanhamos latência, CPU, memória, saúde, volume de OS, tempo por status e falhas técnicas. Habilitamos instrumentação Lambda condicionalmente à configuração compartilhada. Mantemos CloudWatch para logs Lambda e o monitoramento externo via UptimeRobot descrito no guia.

## Consequências

A consulta conjunta dos sinais facilita diagnóstico e acompanhamento do atendimento. A configuração em código permite revisar alterações de dashboards e alertas.

A solução depende de Agents, saída de rede, chaves e SaaS, além de gerar consumo de recursos e custos variáveis de ingestão, retenção e APM. Precisamos controlar cardinalidade e dados enviados. A esteira pode falhar antes do rollout por problemas na instalação dos Agents ou depois dele por falhas no Terraform Datadog; esses casos têm efeitos operacionais diferentes.

Eventos em log são best effort e não equivalem a registro transacional ou entrega exatamente uma vez. O histórico PostgreSQL mantém a base para auditoria de status. A existência dos monitores não comprova coleta ou recebimento de alertas; validar esses caminhos e ajustar os limites continua necessário. Não há orçamento ou evidência de teste de carga presumidos por este registro.

Evidências: [guia operacional](../../OBSERVABILIDADE.md), [Agent](../../../k8s/observability/datadog-agent.yaml), [dashboards](../../../observability/datadog/dashboard.tf) e [logs e monitores](../../../observability/datadog/logs.tf).
