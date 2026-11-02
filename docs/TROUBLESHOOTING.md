# Troubleshooting — gestao-oficina-tc-1

[README](../README.md) · [Execução local](EXECUCAO-LOCAL.md) · [Execução na AWS](EXECUCAO-AWS.md)

## Troubleshooting de outros repositórios da solução

- [Plataforma: bootstrap, EKS, rede e entrada HTTP](https://github.com/tc-organization-26/oficina-k8s-infra/blob/HEAD/docs/TROUBLESHOOTING.md).
- [Banco: RDS, conectividade, credenciais e snapshots](https://github.com/tc-organization-26/oficina-db-infra/blob/HEAD/docs/TROUBLESHOOTING.md).
- [Autenticação: Python, Lambda, CPF e Authorizer](https://github.com/tc-organization-26/oficina-auth-lambda/blob/HEAD/docs/TROUBLESHOOTING.md).

Este guia parte do problema, indica o que conferir e orienta a correção. Execute os comandos na raiz deste repositório, salvo indicação diferente. Cada bloco é um procedimento de diagnóstico independente; os exemplos usam PowerShell.

Nos exemplos AWS, use uma sessão válida e defina:

```powershell
$oficinaEnv = "hom"
$oficinaRegion = "us-east-1"
$env:AWS_DEFAULT_REGION = $oficinaRegion
aws sts get-caller-identity
```

Para produção, altere o ambiente para `prod` e confira a conta antes de prosseguir. As credenciais do GitHub não configuram o terminal; o [guia AWS](EXECUCAO-AWS.md#credenciais-e-ferramentas) explica os pré-requisitos de acesso. Ao compartilhar evidências, informe ambiente, horário, URL/caminho, status HTTP, ID do workflow e mensagem de erro. Remova tokens, senhas e valores de secrets dos trechos compartilhados.

## Assuntos

- [Triagem por camada](#triagem-por-camada)
- [Docker e Docker Compose](#docker-e-docker-compose)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [PostgreSQL e Flyway](#postgresql-e-flyway)
- [Java, Maven e testes](#java-maven-e-testes)
- [API, Swagger, Insomnia e autenticação](#api-swagger-insomnia-e-autenticação)
- [Credenciais AWS e GitHub Actions](#credenciais-aws-e-github-actions)
- [Aplicação no EKS](#aplicação-no-eks)
- [Observabilidade e operação do deploy](#observabilidade-e-operação-do-deploy)
- [Terraform e state S3](#terraform-e-state-s3)

## Triagem por camada

| Sintoma | Comece por |
| --- | --- |
| API local não inicia ou porta não abre | Docker e variáveis de ambiente |
| Build/testes falham | Java, Maven e testes; depois PostgreSQL/Flyway |
| HTTP 400, 401, 403, 404 ou 422 | API, Swagger, Insomnia e autenticação |
| Deployment não fica pronto | Aplicação no EKS |
| Port-forward funciona, mas URL pública falha | Guia da plataforma e guia de autenticação |
| Readiness falha ou login CPF dá erro de banco | PostgreSQL/Flyway e guia RDS |
| API funciona, mas métricas/traces não aparecem | Observabilidade e Terraform Datadog |

## Docker e Docker Compose

### Docker não conecta ao daemon ou compose não é reconhecido

Inicie Docker Desktop e aguarde o engine. `docker version` precisa mostrar Client e Server. Use o comando `docker compose` do ambiente atual; a orientação antiga de usar `docker-compose` v1 não é necessária para este projeto.

```powershell
docker version
docker compose version
docker context show
docker compose ps -a
```

No Windows, confira WSL/virtualização e o engine de contêineres Linux se a imagem Alpine não iniciar. Se houver `permission denied` no socket em Linux, confira a instalação e o acesso ao daemon; a esteira já faz build no GitHub e não depende do Docker no terminal do Lab.

### Dockerfile não encontrado ou imagem continua com código antigo

Execute na raiz, onde estão `Dockerfile`, `.mvn`, `mvnw` e `pom.xml`. Recompile após alterar Java ou configuração que vai dentro da imagem:

```powershell
Get-Item Dockerfile
docker compose up --build -d
docker compose logs --tail=120 app
```

Se a evidência indicar cache incorreto, use `docker compose build --no-cache app` e depois `docker compose up -d app`. Erros de download das imagens/dependências pedem revisão de rede, proxy e certificados; recriar o banco não resolve um build.

### Porta 8081 ou 5433 em uso

Confira outros contêineres e processos:

```powershell
docker ps --format "table {{.Names}}\t{{.Ports}}"
Get-NetTCPConnection -State Listen -LocalPort 8081,5433 -ErrorAction SilentlyContinue
```

Altere apenas a porta publicada do serviço em conflito no Compose, por exemplo `8082:8081` para a API ou `5434:5432` para o banco. A rede entre contêineres continua usando `postgres:5432`. Ajuste `BASE_URL` no Insomnia e a URL JDBC da execução Java/testes na máquina quando mudar a porta do banco.

### App não sobe porque PostgreSQL está unhealthy

O Compose já aguarda o healthcheck do banco. Confira a causa antes de repetir o build:

```powershell
docker compose logs --tail=100 postgres
docker compose exec postgres sh -c 'pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
docker compose logs --tail=150 app
```

`pg_isready` verifica se o servidor aceita conexões; não comprova que a senha da aplicação está correta. Falha de senha e volume antigo são tratadas na seção PostgreSQL.

## Variáveis de ambiente

### .env ausente, .env.txt ou variável vazia

Crie `.env` a partir de `.env.example` somente se ainda não houver um arquivo configurado. No Windows, confira a extensão real:

```powershell
Get-ChildItem -Force -Name .env*
docker compose config --quiet
```

Preencha `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `JWT_SECRET` e `JWT_EXPIRATION_SECONDS`. Os nomes e valores de referência estão no [guia local](EXECUCAO-LOCAL.md#variáveis-da-aplicação). Mensagens de variável não definida pedem correção do arquivo ou do ambiente do terminal. Variáveis já exportadas no shell podem prevalecer sobre `.env`.

Não compartilhe a saída completa de `docker compose config`: ela pode expandir credenciais. `--quiet` valida sem imprimi-las.

### Java direto não lê .env ou aponta para postgres em vez de localhost

O Compose lê `.env`; os testes têm importação configurada. `spring-boot:run` não recebe automaticamente o ambiente do Compose. Defina as variáveis no terminal conforme o guia local. Use `postgres:5432` entre contêineres e `localhost:5433` a partir da máquina. Em Kubernetes, use o endpoint RDS privado recebido no ConfigMap.

### JWT_SECRET vazio ou WeakKeyException

Use uma chave de pelo menos 32 bytes para HS256; caracteres multibyte e espaços podem mudar o valor efetivo. Não acrescente aspas/espaços por engano. O Compose converte `JWT_SECRET` em `SECURITY_JWT_SECRET`. Para Java direto, a validade usa `SECURITY_JWT_EXPIRATION_SECONDS`; o Compose faz essa conversão a partir de `JWT_EXPIRATION_SECONDS`.

Depois de mudar a configuração local, recrie o serviço app. Na AWS, alinhe a chave publicada pela autenticação e a configuração da API; detalhes ficam no guia Lambda. Emitir um token novo não corrige duas chaves diferentes.

## PostgreSQL e Flyway

### Password authentication failed, database does not exist ou credenciais antigas

O volume mantém o banco inicializado: mudar `POSTGRES_USER`, `POSTGRES_DB` ou `POSTGRES_PASSWORD` no `.env` não altera automaticamente usuário, banco ou senha já existentes. Confira os valores usados na primeira inicialização e os logs. Para verificar uma consulta local com a credencial informada pelo Compose, o comando solicita a senha:

```powershell
docker compose exec postgres sh -c 'psql -h 127.0.0.1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -W -c "select 1;"'
```

Se os dados forem descartáveis, a recriação abaixo apaga o volume local deste Compose. Faça backup antes se precisar preservá-los:

```powershell
docker compose down --volumes
docker compose up --build -d
```

Em RDS, não use esse procedimento: confirme endpoint, database e credencial gerenciada no guia do banco.

### Connection refused ou timeout

`Connection refused` costuma indicar host/porta incorretos ou banco ainda indisponível. Timeout na AWS pede revisão de rede/SG, estado do RDS e saída dos workloads. Confira a origem da chamada: `localhost` dentro do Pod/contêiner é ele próprio, não o servidor PostgreSQL. O RDS é privado e não responde diretamente pela internet.

### Flyway falha na validação, checksum ou criação de tabela

Leia a primeira migration que falhou no log. As versões V0–V4 são aplicadas pela API na inicialização; não execute scripts de criação em paralelo. `table already exists` pode indicar criação manual do schema ou conexão ao banco errado. Erro de checksum pode indicar edição de migration já aplicada; restaure a versão original e crie uma nova migration para evoluir o schema.

No banco correto, consulte por uma conexão autorizada:

```sql
select installed_rank, version, description, success
from flyway_schema_history
order by installed_rank;
```

`baseline-on-migrate` não recria o schema nem corrige tabelas incompatíveis. Não rode `clean`, `repair` ou apague `flyway_schema_history` como correção genérica. Em desenvolvimento descartável pode ser apropriado recriar o volume; no RDS, preserve backup e investigue a migration antes de qualquer reparo.

### Cliente de demonstração ou histórico de status ausente

Confira se V3 e V4 foram aplicadas no mesmo banco usado pela Lambda/API. A V3 prepara o CPF fictício `00361937040`; a V4 cria histórico e trigger. Ordens anteriores à V4 não ganham tempos retroativos. Se não há mudança de status, a trigger não cria transição. Veja o [modelo relacional](modelo-relacional/README.md).

## Java, Maven e testes

### Java ausente ou release version 21 not supported

Use JDK 21; uma JRE isolada não fornece `javac`. Confira o Java efetivamente usado pelo wrapper:

```powershell
java -version
javac -version
./mvnw.cmd -version
```

Corrija `JAVA_HOME`/`PATH` e abra outro terminal. A imagem Docker já fornece o JDK de build; ela pode validar a aplicação quando o ambiente Java local não estiver pronto.

### Wrapper não inicia, permission denied ou comandos com CRLF

No Windows, use `./mvnw.cmd` na raiz. Em Linux/macOS, use `./mvnw`; se houver falta de permissão, execute `chmod +x mvnw`. Erro de interpretador com `^M` indica quebra de linha incompatível no script: restaure-o do Git com LF. Não altere a URL do wrapper para contornar um erro sem conferir o arquivo `.mvn/wrapper/maven-wrapper.properties`.

### Dependências não baixam, PKIX ou falha de proxy

Confira conectividade, proxy Maven e certificados da rede/JDK. `./mvnw.cmd -U clean verify` pode repetir a resolução após corrigir a rede. Não desative a validação TLS para contornar erro de certificado. Um build sem cache Docker ajuda apenas quando a causa é cache/imagem, não quando a rede continua bloqueada.

### Testes falham por PostgreSQL ou dados existentes

Os testes usam PostgreSQL em `127.0.0.1:5433`, importam `.env` e consultam suas próprias propriedades de teste. Não aponte testes de integração ao RDS de homologação/produção: eles criam e removem dados.

```powershell
docker compose up -d postgres
./mvnw.cmd clean verify
```

Confira `POSTGRES_USER`, `POSTGRES_PASSWORD` e `POSTGRES_DB`. Se mudou a porta publicada, ajuste a propriedade do Surefire conscientemente; por exemplo `./mvnw.cmd test "-Dspring.datasource.url=jdbc:postgresql://127.0.0.1:5434/oficina_db_2"`. Restrições de unicidade podem indicar dados de uma execução anterior; use um banco local dedicado.

### Testes passam, mas verify falha por cobertura ou arquitetura

O ciclo `verify` também aplica a regra JaCoCo de 80% de linhas no núcleo e os testes ArchUnit. Confira `target/surefire-reports` e `target/site/jacoco/index.html`. Corrija a cobertura ou dependência entre camadas responsável pela falha; não desabilite o check para publicar uma alteração. `-DskipTests` no Dockerfile não substitui o job de testes do CI.

## API, Swagger, Insomnia e autenticação

### Swagger não abre ou readiness ainda não está UP

Confira logs, porta e URL. Localmente, use `http://localhost:8081/swagger-ui/index.html`. Se mudou a porta, atualize o navegador e o Insomnia. Na AWS, Swagger/OpenAPI passam pelo Authorizer; abrir a URL diretamente no navegador sem Bearer pode ser recusado. Para diagnóstico administrativo, use o port-forward descrito adiante.

### Login local retorna erro com CPF ou login AWS rejeita login e senha

São contratos diferentes no mesmo caminho: o Java recebe `{login, senha}`; a Lambda no Gateway recebe `{cpf}`. A collection atual da API usa CPF nos logins AWS. Para acesso direto ao Java, use a credencial fictícia do [guia local](EXECUCAO-LOCAL.md#login-e-chamada-protegida) e adapte o corpo. Erros CPF 400/403/404/500 devem ser investigados no guia de autenticação.

### HTTP 401 ou 403 numa rota protegida

Confirme o cabeçalho `Authorization: Bearer TOKEN`, validade, ambiente, chave e formato do token. Um 403 também pode aparecer no Java sem autenticação válida; não conclua a causa apenas pelo status. Obtenha token novo para o ambiente correto. A API atual exige autenticação, sem regras automáticas por papel/cliente; não atribua toda rejeição a permissões de papel inexistentes.

### HTTP 400, 404, 405, 415, 422 ou 500

| Status | O que conferir |
| --- | --- |
| 400 | JSON válido, tipos, UUIDs, campos obrigatórios e nomes dos enums |
| 404 | URL base, caminho e implantação das rotas; no login CPF também pode ser cliente inexistente |
| 405 | Método HTTP correto para o caminho |
| 415 | `Content-Type: application/json` nas requisições com corpo JSON |
| 422 | Mensagem de `DomainException`: estoque, entidade inexistente, pagamento ou transição de status |
| 500 | Logs pelo request ID; dependência/banco, persistência ou falha técnica |

Consulte o [catálogo completo de rotas](API.md#rotas). Não suponha que todo registro inexistente retorna 404: o tratamento de regra de domínio Java usa 422. Alguns erros não tratados podem chegar como outro status; confira a mensagem e o log.

### Fluxo de OS falha, exclusão é recusada ou requisição repetida causa duplicidade

Confira o status atual, pagamento, vínculos de cliente/veículo, códigos de catálogo e quantidades. A entrada de estoque usa UUID; a baixa usa código. Para entrega, pagamento e transições, siga as regras do domínio e a collection. Exclusões podem ser impedidas por referências no banco. Não repita automaticamente um POST após timeout: consulte antes o que já foi criado. A abertura de OS possui etapas com commits separados e pode ter persistido a ordem antes de uma falha posterior.

### Collection aponta para ambiente antigo ou usa IDs de outra execução

Importe [a collection atual](../src/main/docs/insomnia/collection.yaml), selecione o ambiente e ajuste `BASE_URL`, `TOKEN` e IDs/códigos utilizados. URL do Gateway muda após recriação. Objetos cadastrados em um banco não existem automaticamente em outro. O login por CPF depende do primeiro startup com Flyway.

### Navegador acusa CORS, mas Insomnia funciona

Compare a chamada e o preflight do navegador. Não há configuração CORS explícita de frontend neste fluxo versionado. Um token válido não libera automaticamente uma origem ou método de preflight. Se for integrar um frontend, configure origens, métodos e cabeçalhos coerentemente no Gateway e na aplicação, sem remover a autenticação para resolver a falha.

## Credenciais AWS e GitHub Actions

### ExpiredToken, InvalidClientTokenId ou assinatura inválida

Renove juntos `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` e `AWS_SESSION_TOKEN` na organização (com acesso ao repositório) ou nos Repository secrets. Atualize também o terminal utilizado. Verifique espaços acidentais e relógio da máquina se houver erro de assinatura/tempo. Repita `aws sts get-caller-identity`; ele precisa identificar a conta esperada antes de um novo deploy.

Se o erro persistir no Actions, confira se um secret do Environment está sobrescrevendo o valor renovado da organização. Reinicie a execução após corrigir; não é necessário refazer o bootstrap só para renovar credenciais.

### ParameterNotFound, ResourceNotFound ou recursos do ambiente errado

Confirme `AWS_REGION`, o ambiente (`hom`/`prod`), o prefixo `/oficina/ENV` e a conclusão da etapa anterior. Plataforma, banco e autenticação usam `dev` como padrão Terraform; a observabilidade da API usa `hom`. Selecione explicitamente `hom` ou `prod` para integrar o ambiente implantado. A primeira instalação segue bootstrap → plataforma → banco → autenticação → API → reexecução da autenticação para Datadog.

```powershell
aws ssm get-parameters-by-path --path "/oficina/$oficinaEnv" --recursive --query "Parameters[].Name" --output table
```

Uma lista vazia pode indicar conta/região errada ou pré-requisito ainda ausente. `AccessDenied` significa falta de permissão, não prova que o recurso não existe.

### Deploy pulado, execução aguardando aprovação ou PR sem checks novos

`fase-3` valida; apenas `homologacao` e `main` fazem deploy. O merge para `homologacao` já dispara a implantação em `hom`; não execute outro deploy manual após um merge bem-sucedido. Abrir um PR não dispara uma execução adicional: os workflows atuais não têm gatilho `pull_request`.

Se o job aguardar aprovação, confira as regras do GitHub Environment `homologacao`/`producao`. Se aguardar outro workflow, confira a concorrência configurada. Os runners são `ubuntu-latest`; instruções antigas para instalar um runner Windows self-hosted não se aplicam.

### Workflow não aparece, ferramentas faltam ou execução usa código antigo

Confira se o arquivo está em `.github/workflows`, se a branch selecionada contém a versão esperada e se o workflow permite execução manual. Uma alteração local ainda não enviada ao GitHub não muda o Actions. Na interface, procure o nome do workflow; no CLI, use o nome do arquivo mostrado no README.

`command not found` em um runner indica que a etapa de setup não foi executada ou que foi usada outra branch/versão do workflow. Confira o log dessa etapa antes de instalar ferramentas por fora da esteira. Para uma execução pendente, confira disponibilidade do runner e limites de uso da organização.

### PR automático falha com Resource not accessible by integration ou 403

Confira permissões de escrita e criação de PRs em **Settings → Actions → General → Workflow permissions**, além das políticas da organização. A promoção usa `GITHUB_TOKEN`; `GH_ADMIN_TOKEN` é reservado ao bootstrap. Verifique se a execução de origem passou e se há commits a promover. PR já existente é reutilizado; falha de deploy impede a promoção seguinte.

### IAM nega CreateRole, CreateOpenIDConnectProvider, PassRole ou leitura de secrets

O fluxo educacional atual reutiliza LabRole e não cria roles/OIDC. Tentativas de criação podem indicar código de outra branch ou bootstrap legado. Leia a ação e o recurso negados no log e confira a role configurada; reutilizar LabRole não garante todas as permissões necessárias. A instituição precisa liberar a ação quando houver negação imposta pela conta. Não troque para credenciais de outra conta mantendo o mesmo state.

## Aplicação no EKS

### kubectl não conecta, aponta para docker-desktop ou usa ExecCredential antigo

Configure o cluster publicado no SSM usando AWS CLI atual:

```powershell
$oficinaCluster = aws ssm get-parameter --name "/oficina/$oficinaEnv/eks/cluster-name" --query Parameter.Value --output text
aws eks update-kubeconfig --name $oficinaCluster --region $oficinaRegion
kubectl config current-context
kubectl get nodes
```

Se houver `Unauthorized`, `Forbidden`, timeout ou `ExecCredential v1alpha1`, confira a sessão/conta, versões de AWS CLI/kubectl, endpoint e permissões do criador no guia da plataforma. Regere o kubeconfig; não edite sua versão de API por tentativa. Habilitar Kubernetes no Docker Desktop não prepara o EKS nem altera o cluster do deploy.

### Namespace ausente, ConfigMap/Secret não encontrado ou CreateContainerConfigError

```powershell
kubectl get namespace oficina
kubectl get configmap oficina-api-config -n oficina
kubectl get secret oficina-api-secret ghcr-credentials -n oficina
kubectl get events -n oficina --sort-by=.lastTimestamp
```

O workflow aplica namespace e gera a configuração antes do Deployment. Confira a etapa que consulta SSM/Secrets Manager; não aplique apenas `deployment.yaml` esperando que ele crie essas dependências. Os manifests contêm uma imagem placeholder, substituída pelo CI/CD. Nunca imprima Secret com `-o yaml` para compartilhar um diagnóstico.

### ImagePullBackOff, ErrImagePull, unauthorized ou imagem com SHA inexistente

```powershell
kubectl describe pod -n oficina -l app=oficina-api
kubectl get deployment oficina-api -n oficina -o jsonpath='{.spec.template.spec.containers[0].image}'
```

Confira se o job publicou a imagem por SHA no GHCR, se o nome de organização/repositório está correto e se `GHCR_USERNAME`/`GHCR_TOKEN` permitem `read:packages` e acesso ao pacote. O workflow cria `ghcr-credentials`; o Pod usa esse imagePullSecret. Uma imagem só construída no Docker local não fica disponível nos nós EKS. Os nós privados precisam de saída de rede para baixar a imagem. ECR não é o registry deste fluxo.

### CrashLoopBackOff, OOMKilled, Pending ou rollout excede timeout

```powershell
kubectl get pods -n oficina -o wide
kubectl describe deployment oficina-api -n oficina
kubectl logs deployment/oficina-api -n oficina --tail=150
kubectl logs -n oficina -l app=oficina-api --all-containers=true --previous --tail=100
kubectl get events -n oficina --sort-by=.lastTimestamp
```

`--previous` só tem saída se houver contêiner anterior. Para CrashLoop, leia a primeira exceção: JWT, JDBC, Flyway e configuração são causas distintas. `OOMKilled` pede medir memória/limites; o manifest define request de 512 MiB e limite de 768 MiB, incluindo o efeito do tracer. `Pending` pede conferir recursos, nós e eventos de scheduling no guia EKS. Não aumente timeouts/probes para ocultar falha persistente de inicialização.

### Service não tem endereço externo, NodePort não abre no localhost ou port-forward falha

O Service atual é NodePort `30081`; os nós e o NLB são privados. É esperado não haver IP externo no Service e não funcionar `localhost:30081` no computador. A entrada pública é o Gateway. Para separar aplicação e rede:

```powershell
kubectl get service oficina-api -n oficina
kubectl get endpointslices -n oficina -l kubernetes.io/service-name=oficina-api
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Acesse `http://localhost:18081/swagger-ui/index.html`. Mantenha o terminal do túnel aberto; se 18081 estiver ocupada, use `18082:8081`. Sem endpoints prontos, confira seletor `app=oficina-api`, Pods e readiness. O port-forward bypassa o Gateway, mas o Spring continua protegendo as rotas de negócio. Se o teste direto funciona e a URL pública falha, continue no guia de NLB/VPC Link/Gateway.

### Readiness falha, mas liveness funciona

O readiness inclui o banco; liveness verifica o estado da aplicação. Esse resultado pode indicar JDBC, credenciais, SG ou RDS, não necessariamente processo morto. Aguarde a startup probe concluir e compare eventos/logs. O pipeline exige readiness público `UP` após o deploy e depende das rotas criadas pela autenticação.

### HPA mostra unknown ou não aumenta réplicas

```powershell
kubectl get hpa -n oficina
kubectl describe hpa oficina-api -n oficina
kubectl top pods -n oficina
kubectl get deployment metrics-server -n kube-system
```

O HPA usa CPU/memória e limita réplicas entre 2 e 6; porcentagens dependem dos requests do Deployment. Confira métricas, carga e eventos antes de esperar escala. Metrics Server e Cluster Autoscaler pertencem ao Terraform da plataforma; não instale outra cópia nem aplique o patch inseguro de certificado do Docker Desktop ao EKS. Se HPA pede Pods e eles ficam Pending, investigue capacidade dos nós/autoscaler.

## Observabilidade e operação do deploy

### API funciona, mas o workflow falhou depois do rollout

Veja a etapa exata: o Terraform Datadog roda depois da atualização da API. Uma falha de dashboard/monitor não desfaz o rollout já concluído. Confira o estado da aplicação e corrija a configuração Datadog antes da reexecução; não considere o deploy automaticamente revertido.

### Environment sem DD_API_KEY, DD_APP_KEY ou destino de alertas

Confira o [checklist AWS](EXECUCAO-AWS.md#checklist-inicial), o Environment selecionado e `TF_STATE_BUCKET`. O deploy atual exige esses valores. `DD_SITE` e `DD_API_URL` devem corresponder à mesma conta/região Datadog. Erros 401/403 do Datadog pedem verificar chaves, site e escopos da Application Key, não as credenciais AWS.

### Operator/Agent não sobe ou não há métricas, logs ou traces

```powershell
helm list -n datadog
kubectl get pods -n datadog
kubectl get datadogagent -n datadog
kubectl get deployment datadog-cluster-agent -n datadog
kubectl get daemonset datadog-agent -n datadog
kubectl logs deployment/datadog-operator -n datadog --tail=120
```

Confira CRDs/RBAC, secret `datadog-secret`, tags `service/env/version`, saída HTTPS e Pods instrumentados após o rollout. Métricas dependem de Agents prontos; traces precisam de chamadas novas e tracer ativo; logs precisam de coleta habilitada. Filtros em `env:prod` não exibem eventos `hom`. Consulte a configuração detalhada em [Observabilidade](OBSERVABILIDADE.md).

### Sem dd.trace_id, gráfico de OS vazio ou alerta sem notificação

Sem tracer ativo, `request_id` continua disponível, mas IDs de trace podem não existir. Histórico de status só produz duração após sair da etapa; ordens anteriores à V4 não têm tempos retroativos. Eventos dependem de commits, chegada dos logs, filtros e janela UTC; sua entrega não é exatamente uma vez. Confira o destino real de notificação e integrações da conta. Os listeners Java de domínio apenas registram logs; não enviam email.

### Teste de falha de banco não confirma coleta

O [roteiro de observabilidade](OBSERVABILIDADE.md#validar-a-coleta) cria um Pod temporário em `hom`; um status HTTP de erro sozinho não confirma o teste. Confira `integration.failed` e `os.processing_failed` nos logs e depois a ingestão no Datadog. Evidências ficam em `target/observability-evidence`; falha de túnel fica em `port-forward.log`. A verificação local não confirma a entrega de email remoto. Não aponte esse teste a produção.

### UptimeRobot em erro após recriar o ambiente

Atualize a URL do Gateway e confirme GET do readiness com corpo `status=UP`. Rotas públicas de saúde são GET; uma checagem por HEAD pode ser recusada. O monitor externo é configurado fora do Terraform e não sincroniza automaticamente seu histórico com o Datadog.

## Terraform e state S3

### Terraform ausente, pasta errada ou sintaxe inválida

Use Terraform compatível com o projeto (o CI fixa `1.15.9`, mínimo `1.10.0`). Confira `terraform version`. Os comandos abaixo usam a pasta `observability/datadog` a partir da raiz. Em `terraform.tfvars`, use HCL com valores literais ou variáveis `TF_VAR_*`; expressões Spring como `${VAR:padrao}` não são válidas nesse arquivo. `TF_VAR_*` não substitui os argumentos do backend.

```powershell
terraform -chdir=observability/datadog fmt -check -recursive
terraform -chdir=observability/datadog init -backend=false
terraform -chdir=observability/datadog validate
```

`fmt -check` informa divergência de formato; aplique `fmt` nos arquivos que alterou e revise o diff. O `init -backend=false` valida sem acessar o state S3, mas ainda pode baixar providers/módulos. Ele não prepara a aplicação na AWS.

### Falha no download, checksum ou provider incompatível

Confira rede, proxy, certificados e as versões de Terraform/providers declaradas. Use o lockfile versionado; um erro de checksum pode indicar pacote corrompido, plataforma sem hash registrado ou mudança de dependências. Não apague o lockfile nem rode `init -upgrade` como correção automática: isso pode selecionar versões diferentes. Mudanças de provider devem ser intencionais, revisadas e validadas antes do plano.

### Bucket ausente, AccessDenied ou Backend configuration changed

Conclua o bootstrap e confirme o bucket em `TF_STATE_BUCKET`, conta e região. Uma resposta 403 de `head-bucket` não prova ausência do bucket. O backend precisa de acesso aos objetos do state e do lock. No terminal, informe o mesmo bucket e a mesma chave do CI/CD:

```powershell
$oficinaBucket = "BUCKET_DISTRIBUIDO_PELO_BOOTSTRAP"
$env:TF_VAR_environment = $oficinaEnv
$env:TF_VAR_aws_region = $oficinaRegion
aws s3api head-bucket --bucket $oficinaBucket
terraform -chdir=observability/datadog init -reconfigure `
  -backend-config="bucket=$oficinaBucket" `
  -backend-config="key=oficina/observability/$oficinaEnv.tfstate" `
  -backend-config="region=$oficinaRegion" `
  -backend-config="encrypt=true" `
  -backend-config="use_lockfile=true"
```

`-reconfigure` seleciona a configuração; não transfere automaticamente os recursos de outro state. Se há uma migração real de backend, preserve backup e planeje a migração, em vez de apontar um state vazio para recursos existentes. Pedidos de `secret_suffix`, `KUBE_CONFIG_BASE64` ou backend Kubernetes indicam instruções antigas da fase 2.

### Error acquiring the state lock

Confira execuções ativas no Actions e terminais. Aguarde a operação que possui o lock; interromper o acompanhamento não significa que o processo parou. As esteiras serializam sua própria branch, mas um apply manual ainda pode concorrer.

Somente após confirmar que não há operação ativa e que o backend acima é o correto, remova um lock abandonado com o ID exibido no erro:

```powershell
terraform -chdir=observability/datadog force-unlock ID_EXATO_MOSTRADO_NO_ERRO
```

Não use `-lock=false`, não reutilize um ID de exemplo e não apague o objeto `.tflock` como rotina. Consulte a [referência do force-unlock](https://developer.hashicorp.com/terraform/cli/commands/force-unlock).

### Apply interrompido, recurso já existente ou plano propõe recriação inesperada

Uma execução com falha pode ter criado parte dos recursos. Preserve state e logs, corrija a causa e gere um novo plano no mesmo backend; não reaplique um plano salvo antes de mudanças no ambiente. Se o plano tentar criar tudo novamente, confira a chave, workspace e conta. O projeto usa o workspace padrão e chaves distintas por ambiente.

Recursos existentes fora do state exigem avaliação de importação, não exclusão por tentativa. Revise substituições e destruições antes de aplicar. Não edite o state manualmente para esconder uma divergência.
