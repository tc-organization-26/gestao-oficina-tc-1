# Datadog na Oficina

A implementação usa o tracer Java nativo do Datadog via Single Step Instrumentation (SSI).
Não usa SDK OpenTelemetry nem OpenTelemetry Collector. O Operator instala os Agents;
o Agent coleta métricas do Kubernetes, logs JSON da API e traces do Java.

## O que foi preparado

| Requisito | Implementação |
| --- | --- |
| Latência das APIs Java | Tracer injetado pelo Operator; logs HTTP com rota, método, status e duração; métrica de distribuição para p95 |
| CPU e memória | Agent e kube-state-metrics core; gráficos por pod e monitores de consumo |
| Healthcheck | Actuator liveness/readiness, probes HTTP e check `http.can_connect` por pod |
| Uptime externo | UptimeRobot gratuito, Keyword do readiness publico a cada 5 minutos |
| Falhas de OS | Evento `os.processing_failed` para falhas tecnicas dos casos de uso de OS e orcamento, inclusive fora de HTTP; monitor de logs |
| Logs correlacionados | JSON Logstash, `request_id` e, com o tracer ativo, `dd.trace_id`/`dd.span_id` |
| Volume diário | `os.created` após persistência e commit; rollup diário UTC |
| Tempo por status | Histórico PostgreSQL e evento `os.status_duration` ao sair do status |
| Falhas de integração | `integration.failed` para exceções da integração PostgreSQL; traces JDBC no APM |

Arquivos do Agent: `k8s/observability/datadog-agent.yaml`.
Dashboards, pipeline de logs, métricas e monitores: `observability/datadog/`.
Esses recursos são aplicados pela esteira descrita abaixo após a promoção do código.

## Deploy pelo GitHub Actions

A esteira `.github/workflows/cicd.yml` faz a instalação completa da observabilidade.
Não é necessário executar Helm, kubectl ou Terraform manualmente. O cluster EKS,
o banco, o bucket de state e a autenticação já devem existir.

### Configuração inicial no GitHub

No repositório da API, configure **Settings → Environments → homologacao** e
**producao**. As branches `homologacao` e `main` usam esses ambientes respectivamente.
Mantenha os Secrets AWS e GHCR já usados pela esteira e adicione:

| Tipo | Nome | Valor |
| --- | --- | --- |
| Secret | `DD_API_KEY` | API key da conta Datadog |
| Secret | `DD_APP_KEY` | Application Key com permissões para dashboards, monitores, pipelines e métricas de logs |
| Variable | `TF_STATE_BUCKET` | Bucket S3 já usado pelas esteiras de infraestrutura |
| Variable | `DD_NOTIFICATION_TARGET` | Destino real, como `@voce@dominio.com` ou canal integrado |
| Variable opcional | `DD_SITE` | Site da conta; padrão `datadoghq.com` (US1) |
| Variable opcional | `DD_API_URL` | URL da API correspondente ao site; padrão `https://api.datadoghq.com/` |

`DD_SITE` e `DD_API_URL` devem apontar para a mesma região da conta Datadog.
As credenciais AWS precisam do acesso já utilizado ao EKS/SSM/Secrets Manager e de
leitura/escrita no state `oficina/observability/<hom|prod>.tfstate`, além de
leitura/escrita/exclusão no arquivo `.tflock` correspondente. O acesso Kubernetes
do runner deve permitir instalar o Operator, suas CRDs, RBAC e workloads.

### Ordem de promoção

1. Promova primeiro `oficina-auth-lambda` para `homologacao` e aguarde seu CI/CD.
   Ele adiciona as rotas públicas GET `/actuator/health` e
   `/actuator/health/readiness`, usando a integração Java existente.
2. Promova este repositório para `homologacao` e aguarde seu CI/CD.
3. Valide a aplicação e a coleta antes de promover os dois repositórios para
   `main`, mantendo a mesma ordem.

Os repositórios têm esteiras independentes: a API não dispara nem aguarda a
esteira de autenticação automaticamente. Não é necessário promover os repositórios
`oficina-k8s-infra` e `oficina-db-infra` para estas alterações.
As esteiras de promoção de branches continuam abrindo PRs; o deploy ocorre após
merge/push em `homologacao` ou `main`. Também é possível usar **Run workflow**
nessas duas branches. Branches de desenvolvimento apenas validam nos PRs.

### Etapas automáticas da API

1. Executar testes Java e validar sintaxe Bash, formato e configuração Terraform.
2. Publicar a imagem no GHCR.
3. Verificar a configuração obrigatória do Environment, conectar ao EKS e
   atualizar ConfigMap e Secrets da API.
4. Instalar/atualizar o Operator pelo Helm (chart fixado em `2.31.0`), criar o Secret
   Datadog e aplicar o Agent com o nome real do cluster e a tag do ambiente.
   Aguardar os workloads do Agent e Cluster Agent antes do rollout da API.
5. Publicar o Deployment da API com probes HTTP e tags de ambiente/versão.
   O Flyway executa a migration V4 no banco existente durante a inicialização.
6. Verificar pela URL pública que o readiness responde HTTP 200 e status `UP`.
7. Executar Terraform plan e aplicar esse plano para criar/atualizar os recursos
   do Datadog. O link do dashboard fica no resumo da execução.

O state fica criptografado no S3 com lock nativo e chave separada por ambiente.
Use o workspace padrão: a separação é feita pela chave, sem workspaces `hom`/`prod`.
Se esses recursos já tiverem sido aplicados com state local, migre o state existente
para a chave S3 correspondente antes do primeiro deploy, para evitar duplicação.
Nenhum state local foi encontrado na preparação desta esteira.

Execuções da API na mesma branch são serializadas. Uma falha na instalação do
Datadog impede o rollout da API. Se o Terraform Datadog falhar depois do rollout,
a aplicação já terá sido atualizada; corrija a causa e reexecute a esteira.

Configure o índice de logs na conta Datadog para reter `os.processing_failed` e
`integration.failed`, sem exclusão ou amostragem. Os alertas e métricas dependem
da chegada de novos eventos. Os limites iniciais são p95 de 1000 ms, CPU de
0,45 core por dez minutos e memória de 680 MiB por dez minutos.

## 4. Uptime externo

O uptime externo usa UptimeRobot gratuito, com monitor **Keyword** da URL publica
atual do API Gateway + `/actuator/health/readiness`. A palavra-chave esperada e
`"status":"UP"`; iniciar incidente quando ela **nao existir**. O intervalo e
5 minutos, e o contato e `thssantosbarbosa@gmail.com`.

Use Keyword para verificar o corpo JSON por GET. Na tela atual do plano gratuito,
o monitor HTTP simples usa HEAD, enquanto as rotas publicas versionadas sao GET.
O historico de uptime fica no UptimeRobot; nao ha sincronizacao automatica com o
Datadog. Se o laboratorio recriar o API Gateway, atualize a URL do monitor.

Synthetics foi removido deste Terraform. O proximo apply remove qualquer teste
anterior gerenciado pelo mesmo state. Em 26/10/2026 o state de homologacao tinha
zero testes Synthetics; `DD_ENABLE_SYNTHETICS` no GitHub foi alterada para `false`.
Essa verificacao nao inclui testes manuais em outra conta/state.

Veja o roteiro executavel em [VALIDACAO-OBSERVABILIDADE.md](VALIDACAO-OBSERVABILIDADE.md).

## 5. Validar a coleta 

1. Em Infrastructure/Kubernetes, localize o cluster e os pods `oficina-api`.
2. Em APM, filtre `service:oficina-api env:hom` e execute chamadas autenticadas
   para gerar tráfego; confirme a latência por endpoint e os spans JDBC.
3. Em Logs Explorer, use `service:oficina-api env:hom`. Confira JSON, `request_id`,
   `dd.trace_id` e `dd.span_id`; abra o trace pelo log. Sem tracer ativo, os IDs
   Datadog não existem, mas `request_id` continua funcionando.
4. Envie `X-Request-ID: teste-observabilidade-001` em uma requisição e confirme
   o mesmo valor na resposta e nos logs. IDs ausentes ou inválidos são substituídos
   por UUID. Esse identificador não é automaticamente propagado para outros serviços;
   a propagação distribuída fica a cargo do tracer.
5. Crie uma OS e avance os status pelo fluxo normal. Procure `@event:os.created`
   e `@event:os.status_duration`. O segundo traz `ordem_id`, `os_status` e `duracao_ms`.
6. Confira o dashboard retornado pelo Terraform e teste as notificações dos monitores.
   Em homologação, uma falha controlada de conexão ao banco deve gerar o evento
   `integration.failed`; restaure a configuração logo após o teste.

## Semântica e limites

- Diagnóstico = permanência em `EM_DIAGNOSTICO`; Execução = `EM_EXECUCAO`;
  Finalização = permanência em `FINALIZADA` até `ENTREGUE`.
- O tempo só entra no gráfico quando o status termina. Uma ordem que ainda está
  no status não participa da média. Retornos ao diagnóstico geram novas passagens.
- Não se inventam horários para ordens anteriores à V4. A primeira transição de
  uma ordem antiga inicia o acompanhamento; a duração anterior fica desconhecida.
- O volume conta OS persistidas, mesmo se uma etapa posterior da requisição falhar.
  O fluxo atual possui commits separados; esta alteração não torna todo o caso
  de uso uma única transação. Rejeições de negócio 4xx não são falhas técnicas 5xx.
- O histórico é transacional. O envio dos logs é best effort: não há outbox nem
  garantia de entrega exatamente uma vez. O histórico SQL permite auditoria caso
  a coleta sofra interrupções. Escritas diretas por SQL geram histórico, mas não
  os eventos de log publicados pelo decorador Java.
- A integração explicitamente identificada nos eventos de erro é PostgreSQL.
  Novas integrações precisam emitir `integration.failed` com `integracao` ao falhar.
  Os listeners de notificação atuais apenas escrevem logs; não enviam e-mail real.
- As Lambdas tem instrumentacao preparada em `oficina-auth-lambda`: layers oficiais,
  logs JSON e servicos `oficina-login` e `oficina-authorizer`. A ativacao ocorre na
  esteira ao encontrar o secret compartilhado `oficina-ENV/datadog`, alimentado
  pela esteira da API com a **mesma DD_API_KEY existente**. Nao e preciso cadastrar
  DD_API_KEY/DD_APP_KEY no repositorio Lambda. A LabRole precisa de acesso de leitura
  ao secret; login precisa de saida HTTPS pelo NAT. Publique API e reexecute o deploy
  Lambda para habilitar a instrumentacao. O dashboard inclui p95 das duas funcoes e
  o monitor de integracoes inclui PostgreSQL/Secrets Manager das Lambdas.
  Authorizer e API Java podem ter traces separados: nao ha chamada HTTP direta
  instrumentada do authorizer para Java atraves da autorizacao do API Gateway.
- O rollup de volume diário usa dias UTC, indicado no título do gráfico.
- Alteracoes locais dependem de promocao e deploy; nao comprovam por si so coleta no ambiente.

## Referências

- [Datadog SSI no Kubernetes](https://docs.datadoghq.com/tracing/trace_collection/single-step-apm/kubernetes/)
- [Logs estruturados no Spring Boot](https://docs.spring.io/spring-boot/reference/features/logging.html)
- [Métricas derivadas de logs no provider Datadog](https://registry.terraform.io/providers/datadog/datadog/latest/docs/resources/logs_metric)
- [Instrumentacao Python Lambda](https://docs.datadoghq.com/serverless/aws_lambda/instrumentation/python/)
