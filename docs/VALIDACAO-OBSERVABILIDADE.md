# Validacao de observabilidade em homologacao

## Credenciais existentes

Reutilize os Secrets GitHub `DD_API_KEY` e `DD_APP_KEY` do repositorio da API.
Nao crie outras chaves e nao copie os secrets para o repositorio Lambda.
A esteira da API disponibiliza a mesma API key no secret AWS `oficina-hom/datadog`,
para as Lambdas lerem em execucao. O valor nao passa pelo Terraform.
O secret Kubernetes `datadog-secret` continua sendo usado pelo Agent.

Publique primeiro as alteracoes da API, depois reexecute o deploy Lambda para
ativar a instrumentacao. Na instalacao inicial, o deploy Lambda anterior a API
continua necessario para publicar as rotas de healthcheck; depois reexecute-o.
O fluxo de branches existente continua sendo usado; nao foi criada outra esteira.

## UptimeRobot

Monitor gratuito: **Oficina HOM - Readiness publico**, tipo **Keyword**.
Use a URL atual do API Gateway + `/actuator/health/readiness`.
Palavra-chave: `"status":"UP"`; iniciar incidente quando **nao existir**.
Intervalo: 5 minutos. E-mail: `thssantosbarbosa@gmail.com`.
O tipo Keyword verifica o corpo por GET; evita a limitacao HEAD do HTTP simples
gratuito diante das rotas publicas GET do Gateway.
Uptime e incidentes ficam no UptimeRobot. Datadog continua com o check interno.
Nao foi implementada sincronizacao entre os dois produtos.

O laboratorio pode recriar o Gateway com outro ID. Confira a URL no resumo do deploy
e atualize o monitor nesse caso. Um readiness 404 significa que as rotas/aplicacao
ainda nao foram publicadas; nao comprova saude do sistema.

## Gerar dados e uma falha real sem parar a API

Requisitos locais: Python 3, AWS CLI e kubectl autenticados em homologacao.
Nenhuma biblioteca Python adicional nem chave Datadog local e necessaria.
Use a URL publicada no resumo do deploy:

```powershell
python scripts/observability/validate.py run --url https://API-ID.execute-api.us-east-1.amazonaws.com --fault
```

O script verifica `env:hom` no Deployment e compara a URL com o SSM antes de gravar.
Usa o CPF ficticio do seed, cria um veiculo e uma OS e percorre Diagnostico,
Aguardando aprovacao, aprovacao do orcamento, Execucao, Finalizada, pagamento e Entregue.
Os registros de teste permanecem em homologacao e sao identificados por `obs-*`.
Se o seed tiver sido alterado, use `OFICINA_TEST_CPF` e `OFICINA_TEST_CLIENT_ID`.
Opcionalmente forneca um JWT em `OFICINA_TOKEN`; nao envie tokens pelo chat.

Com `--fault`, o script cria um pod temporario com a mesma imagem e banco apontando
para `127.0.0.1:1`. Ele nao tem o label `app` selecionado pelo Service, nem modifica
o Deployment ou o banco real. Flyway e acesso a metadados Hibernate sao desativados
somente nesse pod para permitir iniciar a aplicacao sem banco. Uma consulta de OS
deve retornar 500 e gerar `os.processing_failed` e `integration.failed` de PostgreSQL.
O pod e excluido em `finally`. Se o processo for interrompido, o pod encerra apos
10 minutos pelo activeDeadlineSeconds; remova o pod encerrado pelo nome `obs-*-fault`.
Os alertas de teste devem ser recebidos pelo contato configurado.

## Conferir as evidencias

O resultado fica em `target/observability-evidence/evidence.json` e contem somente
IDs, status HTTP e campos de telemetria selecionados. Nao salva JWT, API key,
senha ou corpo do login. O script verifica os eventos e os IDs do tracer nos logs
dos pods; isso sozinho nao comprova a ingestao no Datadog.

1. Copie a busca `datadog_query` do arquivo e abra no Logs Explorer. Confirme os
   eventos `os.created`, `os.status_duration`, `os.processing_failed` e `integration.failed`.
2. Abra um log com `dd.trace_id`/`dd.span_id`, clique no trace e confirme os spans HTTP/JDBC.
3. No dashboard, selecione a janela do teste e confirme volume +1 e as medias de
   `EM_DIAGNOSTICO`, `EM_EXECUCAO` e `FINALIZADA`. Outros usuarios podem gerar mais
   eventos nessa janela; os tempos so entram quando a ordem sai do status.
4. Confira aumento de falhas da integracao `postgresql` e os dois monitores em Alert:
   `[hom] Falha no processamento de OS` e `[hom] Falha em integracao`.
5. Confirme o recebimento dos emails. O contato nas mensagens do monitor nao prova
   a entrega. Depois da janela de cinco minutos sem novos erros, confira recuperacao.
6. Chame o login publico e uma rota autenticada. Confira `oficina-login` e
   `oficina-authorizer` no APM e nos logs, alem de `oficina-api`.

Se precisar reler logs sem criar outra OS, mantenha o diretorio de evidencias e use:

```powershell
python scripts/observability/validate.py verify --url https://API-ID.execute-api.us-east-1.amazonaws.com
```

Guarde capturas do monitor UptimeRobot, de um trace aberto pelo log, dos graficos
e do email recebido. Nao marque os requisitos como validados antes dessas verificacoes.
