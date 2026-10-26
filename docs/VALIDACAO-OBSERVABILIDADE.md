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

## Provocar somente uma falha de banco

Este script testa uma falha de postgres no ambiente AWS.

Com Python 3, AWS CLI e kubectl autenticados em homologacao, execute:

```powershell
python scripts/observability/validate.py run --url https://API-ID.execute-api.us-east-1.amazonaws.com --fault
```

O modo run sempre testa a falha de banco; --fault continua aceito por compatibilidade.
O script confere env:hom e a URL publicada no SSM, cria um unico pod temporario com
banco apontando para 127.0.0.1:1 e aguarda o liveness responder antes do port-forward.
Esse pod nao tem o label app selecionado pelo Service. O Deployment e o banco real
nao sao alterados. Flyway, metadados Hibernate e inicializacao Hikari sao ajustados
somente nesse pod para a aplicacao iniciar sem o banco.

O script obtem um token pelo login publico usando o cliente ficticio do seed
(OFICINA_TEST_CPF pode sobrescrever o CPF). Se preferir, forneca OFICINA_TOKEN no
ambiente. Tokens e corpos do login nao sao registrados. Depois faz somente uma
consulta de OS no pod com banco invalido, sem criar nenhuma OS. A mesma falha
emite integration.failed com integracao:postgresql e os.processing_failed.
O criterio de sucesso e encontrar os dois eventos no log da mesma requisicao;
um HTTP de erro sozinho nao comprova falha de banco. O status HTTP recebido fica
na evidencia: o tratamento de erros do Spring pode devolver 403 em vez de 500.

O pod e removido em finally. Se o processo for interrompido, o pod encerra apos
10 minutos pelo activeDeadlineSeconds; remova o pod encerrado obs-*-fault.

## Conferir as evidencias

O resultado fica em target/observability-evidence/evidence.json. Em caso de falha
do tunel, consulte tambem port-forward.log nesse diretorio.

1. Copie datadog_query do arquivo e procure no Logs Explorer.
2. Confirme integration.failed com integracao:postgresql e os.processing_failed.
   Confira aumento nos graficos Falhas por integracao e Falhas de processamento de OS.
3. Confira os monitores [hom] Falha em integracao e [hom] Falha no processamento de OS
   em Alert e os emails recebidos.
4. Apos cinco minutos sem novos erros, confira a recuperacao do monitor.

O script confirma o evento nos logs do pod, nao a ingestao remota nem a entrega
do email. Para conferir novamente a evidencia salva, sem criar outro pod:

```powershell
python scripts/observability/validate.py verify --url https://API-ID.execute-api.us-east-1.amazonaws.com
```
