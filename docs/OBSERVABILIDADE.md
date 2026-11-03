# Datadog

[README](../README.md) · [Índice da documentação](../README.md#navegação) · [Execução no laboratório AWS](EXECUCAO-LAB-AWS.md)

O Datadog foi integrado à aplicação para centralizar a coleta e a análise de métricas, logs e traces. Essa observabilidade permite acompanhar a saúde dos serviços, medir a latência das APIs, identificar falhas e analisar o consumo de recursos, facilitando o diagnóstico de problemas e o acompanhamento do ambiente em produção.

A implementação usa o tracer Java nativo do Datadog via Single Step Instrumentation (SSI). O Operator instala os Agents; o Agent coleta métricas do Kubernetes, logs JSON da API e traces do Java.

## Dados fornecidos sobre a solução

| Requisito | Implementação |
| --- | --- |
| Latência das APIs Java | Tracer injetado pelo Admission Controller do Datadog; logs HTTP com rota, método, status e duração; métrica de distribuição para p95 |
| CPU e memória | Agent e kube-state-metrics core; gráficos por pod e monitores de consumo |
| Healthcheck | Actuator liveness/readiness, probes HTTP e check `http.can_connect` por pod |
| Uptime externo | UptimeRobot gratuito, Keyword do readiness público a cada 5 minutos |
| Falhas de OS | Evento `os.processing_failed` para falhas técnicas dos casos de uso de OS e orçamento, inclusive fora de HTTP; monitor de logs |
| Logs correlacionados | JSON Logstash, `request_id` e, com o tracer ativo, `dd.trace_id`/`dd.span_id` |
| Volume diário | `os.created` após persistência e commit; rollup diário UTC |
| Tempo por status | Histórico PostgreSQL e evento `os.status_duration` ao sair do status |
| Falhas de integração | `integration.failed` para exceções da integração PostgreSQL; traces JDBC no APM |

Arquivos do Agent: `k8s/observability/datadog-agent.yaml`. Dashboards, pipeline de logs, métricas e monitores: `observability/datadog/`. Esses recursos são aplicados pela esteira descrita abaixo após a promoção do código.

## Deploy pelo GitHub Actions

A esteira `.github/workflows/cicd.yml` faz a instalação completa da observabilidade. Não é necessário executar Helm, kubectl ou Terraform manualmente. O cluster EKS, o banco, o bucket de state e a autenticação já devem existir.

### Configuração e permissões

Os nomes das configurações, os locais de cadastro e os valores padrão estão no [checklist do guia AWS](EXECUCAO-LAB-AWS.md#checklist-inicial). Durante o deploy, a esteira da API armazena a chave de API do Datadog no Secrets Manager, para que as funções Lambda do mesmo ambiente possam utilizá-la.

As credenciais AWS precisam do acesso ao EKS/SSM/Secrets Manager e de leitura/escrita no state `oficina/observability/<hom|prod>.tfstate`, além de leitura/escrita/exclusão no arquivo `.tflock` correspondente. O acesso Kubernetes do runner deve permitir instalar o Operator, suas CRDs, RBAC e workloads.

### Ordem de promoção

Na primeira instalação, siga a [ordem completa dos quatro projetos](EXECUCAO-LAB-AWS.md#ordem-da-primeira-implantação), incluindo a reexecução da autenticação após a API para habilitar suas layers Datadog.

Implante a API e valide a coleta antes de promover para produção; repita o deploy Lambda se a instrumentação compartilhada tiver mudado. Rede e banco só precisam de nova implantação quando houver alterações nesses componentes. O [merge dispara o deploy automaticamente](EXECUCAO-LAB-AWS.md#primeiro-deploy-e-reexecuções).

### Injeção do tracer

O tracer Java registra a duração e o contexto das requisições e chamadas JDBC para consulta no APM. No projeto, ele é carregado automaticamente pela Single Step Instrumentation (SSI): ao criar um Pod, o Admission Controller do Datadog adiciona init containers, volumes e configurações que iniciam o Java com o tracer. Os traces seguem pelo Datadog Agent até o Datadog.

A configuração está nos seguintes arquivos:

- `k8s/observability/datadog-agent.yaml`: habilita a instrumentação no namespace `oficina`.
- `k8s/cloud/deployment.yaml`: habilita a admissão com `admission.datadoghq.com/enabled: "true"`, define serviço, ambiente e versão e ativa correlação de logs e métricas de runtime.
- `main.tf` de `oficina-k8s-infra`: permite TCP 8000 do control plane para os nós, dando acesso ao webhook.
- `scripts/ci/install-datadog.sh`: instala o Operator, configura os Agents e valida a injeção com `--dry-run=server`, conferindo os init containers Datadog sem criar um Pod persistente. Se essa validação falhar, o rollout da API não prossegue.

Implante a infraestrutura antes da API. A injeção ocorre na criação dos Pods; Pods existentes precisam de rollout. Após o deploy, gere chamadas autenticadas e confira os traces no APM e `dd.trace_id`/`dd.span_id` nos logs do ambiente (`env:hom` ou `env:prod`).

### Etapas automáticas da inicialização

1. Executar testes Java e validar sintaxe Bash, formato e configuração Terraform.
2. Publicar a imagem no GHCR.
3. Verificar os secrets e variables obrigatórios do repositório, conectar ao EKS e atualizar ConfigMap e Secrets da API.
4. Instalar/atualizar o Operator pelo Helm (chart fixado em `2.31.0`), criar o Secret Datadog e aplicar o Agent com o nome real do cluster e a tag do ambiente. Aguardar os workloads do Agent e Cluster Agent e confirmar a injeção do tracer em um pod em dry-run antes do rollout da API.
5. Publicar o Deployment da API com probes HTTP e tags de ambiente/versão. O Flyway executa as migrations no banco existente durante a inicialização.
6. Verificar pela URL pública que o readiness responde HTTP 200 e status `UP`.
7. Executar Terraform plan e aplicar esse plano para criar/atualizar os recursos do Datadog. O link do dashboard fica no resumo da execução.

O state fica criptografado no S3 com lock nativo e chave separada por ambiente. Use o workspace padrão: a separação é feita pela chave, sem workspaces `hom`/`prod`. Se esses recursos já tiverem sido aplicados com state local, migre o state existente para a chave S3 correspondente antes do primeiro deploy, para evitar duplicação.

Execuções da API na mesma branch são serializadas. Uma falha na instalação do Datadog impede o rollout da API. Se o Terraform Datadog falhar depois do rollout, a aplicação já terá sido atualizada; corrija a causa e reexecute a esteira.

Configure o índice de logs na conta Datadog para reter `os.processing_failed` e `integration.failed`, sem exclusão ou amostragem.

Os alertas e métricas dependem da chegada de novos eventos. Os limites iniciais são p95 de 1000 ms, CPU de 0,45 core por dez minutos e memória de 680 MiB por dez minutos.

Os monitores de CPU e memória aguardam cinco minutos (`new_group_delay = 300`) antes de avaliar um novo grupo `pod_name`, reduzindo alertas durante a inicialização. A média continua usando a janela dos últimos dez minutos.

## Validar a coleta

Repita as verificações em homologação e produção, selecionando o cluster, a URL e o dashboard do ambiente correspondente. Use `env:hom` para homologação e `env:prod` para produção; os monitores correspondentes têm prefixos `[hom]` e `[prod]`. O teste de falha da próxima seção usa `hom` por padrão; para produção, selecione explicitamente `--environment prod` e o cluster correspondente.

1. Em Infrastructure/Kubernetes, localize o cluster e os pods `oficina-api`.
2. Em APM, filtre `service:oficina-api env:hom` em homologação ou `service:oficina-api env:prod` em produção e execute chamadas autenticadas para gerar tráfego; confirme a latência por endpoint e os spans JDBC.
3. Em Logs Explorer, use `service:oficina-api env:hom` em homologação ou `service:oficina-api env:prod` em produção. Confira JSON, `request_id`, `dd.trace_id` e `dd.span_id`; abra o trace pelo log. Sem tracer ativo, os IDs Datadog não existem, mas `request_id` continua funcionando.
4. Envie `X-Request-ID: teste-observabilidade-001` em uma requisição e confirme o mesmo valor na resposta e nos logs. IDs ausentes ou inválidos são substituídos por UUID. Esse identificador não é automaticamente propagado para outros serviços; a propagação distribuída fica a cargo do tracer.
5. Crie uma OS e avance os status pelo fluxo normal. Procure `@event:os.created` e `@event:os.status_duration`. O segundo traz `ordem_id`, `os_status` e `duracao_ms`.
6. Confira o dashboard retornado pelo Terraform e teste as notificações dos monitores. Para verificar falhas de banco no ambiente selecionado, use o teste a seguir, que cria um pod temporário isolado da aplicação.

### Como provocar falha no banco implantado para validar a coleta

Este script testa uma falha de PostgreSQL em um pod temporário isolado na AWS. O ambiente padrão é homologação; produção exige `--environment prod`. O pod aponta para `127.0.0.1:1`, sem modificar o RDS ou o Deployment da API. A falha gera logs e pode disparar os alertas do ambiente selecionado.

Com Python 3, AWS CLI e kubectl autenticados em homologação, execute:

```powershell
python scripts/observability/validate.py run --url https://API-ID.execute-api.us-east-1.amazonaws.com --fault
```

Para produção, conecte o kubectl ao cluster publicado em `/oficina/prod/eks/cluster-name` e execute:

```powershell
$cluster = aws ssm get-parameter --name /oficina/prod/eks/cluster-name --region us-east-1 --query Parameter.Value --output text
aws eks update-kubeconfig --name $cluster --region us-east-1
python scripts/observability/validate.py run --environment prod --url https://API-ID-PROD.execute-api.us-east-1.amazonaws.com --output target/observability-evidence-prod --fault
```

Use a URL real de produção. O diretório de saída separado preserva as evidências de homologação. Para conferir novamente essas evidências com `verify`, use os mesmos `--environment prod` e `--output target/observability-evidence-prod`.

O modo run sempre testa a falha de banco; --fault continua aceito por compatibilidade. O script confere a tag `env` do ambiente selecionado e sua URL publicada no SSM, cria um único pod temporário com banco apontando para 127.0.0.1:1 e aguarda o liveness responder antes do port-forward. Flyway, metadados Hibernate e inicialização Hikari são ajustados somente nesse pod para a aplicação iniciar sem o banco.

O script obtém um token pelo login público usando o cliente fictício. Depois faz somente uma consulta de OS no pod com banco inválido, sem criar nenhuma OS. A mesma falha emite `integration.failed` com `integracao:postgresql` e `os.processing_failed`.

O critério de sucesso é encontrar os dois eventos no log da mesma requisição; um HTTP de erro sozinho não comprova falha de banco. O status HTTP recebido fica na evidência: o tratamento de erros do Spring pode devolver 403 em vez de 500.

O pod é removido em finally. Se o processo for interrompido, o pod encerra após 10 minutos pelo activeDeadlineSeconds; remova o pod encerrado obs-*-fault.

O resultado do teste fica em target/observability-evidence/evidence.json. Em caso de falha do túnel, consulte também port-forward.log nesse diretório.

1. Copie datadog_query do arquivo e procure no Logs Explorer.
2. Confirme `integration.failed` com `integracao:postgresql` e `os.processing_failed`. Confira aumento nos gráficos `Falhas por integracao` e `Falhas de processamento de OS`.
3. Confira os monitores `Falha em integracao` e `Falha no processamento de OS` com o prefixo do ambiente (`[hom]` ou `[prod]`) em Alert e os emails recebidos.

## Observações importantes e limites

- Diagnóstico = permanência em `EM_DIAGNOSTICO`; Execução = `EM_EXECUCAO`; Finalização = permanência em `FINALIZADA` até `ENTREGUE`.
- O tempo só entra no gráfico quando o status termina. Uma ordem que ainda está no status não participa da média. Retornos ao diagnóstico geram novas passagens.
- O volume conta OS persistidas, mesmo se uma etapa posterior da requisição falhar. O fluxo atual possui commits separados; esta alteração não torna todo o caso de uso uma única transação. Rejeições de negócio 4xx não são falhas técnicas 5xx.
- O histórico é transacional. O envio dos logs é best effort: não há outbox nem garantia de entrega exatamente uma vez. O histórico SQL permite auditoria caso a coleta sofra interrupções. Escritas diretas por SQL geram histórico, mas não os eventos de log publicados pelo decorador Java.
- A integração explicitamente identificada nos eventos de erro é PostgreSQL. Novas integrações precisam emitir `integration.failed` com `integracao` ao falhar. Os listeners de notificação atuais apenas escrevem logs; não enviam e-mail real.
- As Lambdas têm instrumentação preparada em `oficina-auth-lambda`: layers oficiais, logs JSON e serviços `oficina-login` e `oficina-authorizer`. A ativação ocorre na esteira ao encontrar o secret compartilhado `oficina-ENV/datadog`, alimentado pela esteira da API com a **mesma DD_API_KEY existente**. Não é preciso cadastrar DD_API_KEY/DD_APP_KEY no repositório Lambda. A LabRole precisa de acesso de leitura ao secret; login precisa de saída HTTPS pelo NAT. Publique API e reexecute o deploy Lambda para habilitar a instrumentação. O dashboard inclui p95 das duas funções e o monitor de integrações inclui PostgreSQL/Secrets Manager das Lambdas. Authorizer e API Java podem ter traces separados: não há chamada HTTP direta instrumentada do authorizer para Java através da autorização do API Gateway.
- O rollup de volume diário usa dias UTC, indicado no título do gráfico.
- Alterações locais dependem de promoção e deploy; não comprovam por si só coleta no ambiente.

## Uptime externo

O uptime externo usa UptimeRobot gratuito, com monitor **Keyword** da URL pública atual do API Gateway + `/actuator/health/readiness`. A palavra-chave esperada é `"status":"UP"`; iniciar incidente quando ela **não existir**. O intervalo é 5 minutos. Crie um monitor separado para `hom` e outro para `prod`, cada um com a URL do respectivo API Gateway. Configure o contato responsável pelo ambiente.

Configure o monitor para verificar o corpo JSON por GET. As rotas públicas de saúde versionadas são GET; uma verificação por HEAD pode ser recusada pelo Gateway. O histórico de uptime fica no UptimeRobot; não há sincronização automática com o Datadog. Se o API Gateway for recriado, atualize a URL do monitor.

## Referências para maiores detalhes

- [Datadog SSI no Kubernetes](https://docs.datadoghq.com/tracing/trace_collection/single-step-apm/kubernetes/)
- [Logs estruturados no Spring Boot](https://docs.spring.io/spring-boot/reference/features/logging.html)
- [Métricas derivadas de logs no provider Datadog](https://registry.terraform.io/providers/datadog/datadog/latest/docs/resources/logs_metric)
- [Instrumentação Python Lambda](https://docs.datadoghq.com/serverless/aws_lambda/instrumentation/python/)
