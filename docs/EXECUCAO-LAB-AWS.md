# Execução no laboratório AWS — gestao-oficina-tc-1

[README](../README.md) · [Execução local e comandos manuais](EXECUCAO-LOCAL.md) · [Referência técnica](API.md)

Para preparar o computador, consulte a [instalação das ferramentas](INSTALACAO-FERRAMENTAS.md). Para executar fora do Academy/Vocareum, siga a [preparação de conta AWS comum](https://github.com/tc-organization-26/oficina-k8s-infra/blob/HEAD/docs/CONTA-AWS-COMUM.md), incluindo as adaptações de IAM, Terraform e workflows.

Guia de configuração, implantação e validação deste componente na AWS. Os comandos de verificação usam `hom`; para produção, troque os caminhos/nomes para `prod` e selecione `main`.

> **Observação — adaptações educacionais:** o código atual foi adaptado para AWS Academy/Vocareum: utiliza credenciais temporárias e reutiliza a `LabRole` no EKS, nos nós e nas Lambdas, sem criar roles IAM ou provider OIDC. O bootstrap procura essa role e o workflow EKS aplica um patch específico para as restrições do ambiente. Se for utilizar uma conta AWS comum, prepare as roles e permissões necessárias, o Terraform e os workflows antes de seguir este roteiro. Os nomes `LAB_ROLE_ARN`, `lab_role_arn` e dos scripts foram preservados porque correspondem ao código.

## Credenciais e ferramentas

Os quatro repositórios devem existir no GitHub e conter o código que será implantado. Configure os acessos do checklist antes de iniciar o componente correspondente.

Você pode acompanhar o deploy pela interface do GitHub, sem ferramentas locais. Para usar os exemplos do terminal, instale GitHub CLI e autentique com `gh auth login`.

Para as verificações AWS, instale AWS CLI; para EKS, instale também kubectl. Para essas verificações, use as credenciais da sessão nas variáveis `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` e `AWS_SESSION_TOKEN`, configure a região em `AWS_DEFAULT_REGION` e confirme a conta com `aws sts get-caller-identity`.

Os exemplos usam PowerShell e a região `us-east-1`.

## Checklist inicial

Antes da primeira implantação, confira a configuração abaixo.
Os secrets AWS nos repos já devem estar disponíveis antes da criação do bucket s3 versionado para guardar states.

| Conferir | Onde cadastrar | Configuração |
| --- | --- | --- |
| [ ] Credenciais AWS válidas | Organization secrets com acesso aos quatro repositórios, ou Repository secrets em cada um | `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` e `AWS_SESSION_TOKEN`; renove os três juntos quando a sessão expirar. |
| [ ] Token de administração GitHub | Repository secret em `oficina-k8s-infra` | `GH_ADMIN_TOKEN`, com leitura dos quatro repositórios e permissão para administrar Actions Variables. |
| [ ] Chave de assinatura JWT | Environment secret em `oficina-auth-lambda` | `JWT_SECRET`, com pelo menos 32 bytes aleatórios, no Environment `homologacao` e, quando utilizar produção, em `producao`. A API lê essa chave do Secrets Manager. |
| [ ] Leitura da imagem GHCR | Environment secrets em `gestao-oficina-tc-1` | `GHCR_USERNAME` e `GHCR_TOKEN`, com `read:packages` e acesso ao pacote, nos Environments utilizados. A publicação usa o `GITHUB_TOKEN` automático. |
| [ ] Chaves Datadog | Environment secrets em `gestao-oficina-tc-1` | `DD_API_KEY` e `DD_APP_KEY` (permissões para dashboards, monitores, pipelines e métricas de logs), nos Environments utilizados. |
| [ ] Destino e região Datadog | Environment variables em `gestao-oficina-tc-1` | `DD_NOTIFICATION_TARGET` com um destino real, como `@voce@dominio.com`; `DD_SITE` (padrão `datadoghq.com`) e `DD_API_URL` (padrão `https://api.datadoghq.com/`) devem corresponder ao mesmo site. Configure `DD_SITE` correspondente em `oficina-auth-lambda` quando necessário. |
| [ ] Permissões de promoção | Settings → Actions → General nos quatro repositórios | Habilitar escrita do `GITHUB_TOKEN` e criação de Pull Requests, conforme a seção de branches. |

Para cadastrar Repository secrets/variables, use **Settings → Secrets and variables → Actions**. Os secrets da organização ficam em **Organization settings → Secrets and variables → Actions**.

`AWS_REGION`, `TF_STATE_BUCKET` e `LAB_ROLE_ARN` são distribuídas pela criação do bucket, que ocorre primeiro. Confirme essas variables depois que ele concluir; Secrets/variables de Environment devem corresponder ao ambiente utilizado. O deploy da API exige a configuração Datadog. Veja em [configuração Datadog](OBSERVABILIDADE.md)

## Branches e ambientes

| Branch | Execução automática ou manual | Ambiente AWS | GitHub Environment |
| --- | --- | --- | --- |
| `fase-3` | Valida e abre/mantém PR para `homologacao`; não faz deploy | Nenhum deploy | — |
| `homologacao` | Valida, faz deploy e abre/mantém PR para `main` após sucesso | `hom` | `homologacao` |
| `main` | Valida e faz deploy | `prod` | `producao` |

Os merges são manuais. `workflow_dispatch` respeita a mesma condição de deploy: escolher `fase-3` apenas valida. Os workflows não têm gatilho `pull_request`; os checks rodam por push na branch de origem ou por execução manual.

Em **Settings → Actions → General → Workflow permissions**, habilite **Read and write permissions** e **Allow GitHub Actions to create and approve pull requests**.

A branch main é protegida e pode receber commits apenas via PR.

### Primeiro deploy e reexecuções

Depois da validação em `fase-3`, faça o merge do PR para `homologacao` quando as dependências deste componente estiverem prontas. **O próprio merge gera um push em `homologacao` e já dispara a validação e o deploy em `hom`.** Acompanhe essa execução em Actions e aguarde seu sucesso antes de avançar para o próximo repositório.

**Não é necessário executar Run workflow depois de um merge bem-sucedido.** Use a execução manual como alternativa quando a branch já contiver o código desejado, ou para reexecutar o deploy após correções. Fazer merge em `main` dispara o deploy de produção da mesma forma.

## Ordem da primeira implantação

1. `oficina-k8s-infra`: executar `bootstrap.yml` para preparar o backend e as variables.
2. `oficina-k8s-infra`: fazer merge para `homologacao` e aguardar `terraform.yml` criar rede, EKS e entrada HTTP.
3. `oficina-db-infra`: fazer merge para `homologacao` e aguardar `terraform.yml` criar o RDS.
4. `oficina-auth-lambda`: fazer merge para `homologacao` e aguardar `cicd.yml` publicar funções, chave JWT e rotas e habilitar Datadog.
5. `gestao-oficina-tc-1`: fazer merge para `homologacao` e aguardar `cicd.yml` publicar a API, aplicar Flyway e instalar observabilidade.

Execute os passos em sequência e no mesmo ambiente. As esteiras são independentes: uma não dispara a próxima. O passo 4 pode ser implantado antes das tabelas; o teste de login só deve ocorrer após o Flyway do passo 5.

## Deploy deste repositório

Antes do deploy da aplicação, rede/EKS, RDS e autenticação devem existir. O workflow lê cluster e banco pelo SSM e as credenciais RDS/JWT pelo Secrets Manager, criando ConfigMap e Secrets no namespace `oficina`.

Os secrets e variables necessários estão no [checklist inicial](#checklist-inicial). Este workflow usa `AWS_REGION` e `TF_STATE_BUCKET`; não lê `LAB_ROLE_ARN`. O state da observabilidade fica em `oficina/observability/ENV.tfstate`. Consulte o [guia Datadog](OBSERVABILIDADE.md) para as permissões e a validação da coleta.

O pipeline testa com PostgreSQL temporário, valida observabilidade, publica a imagem por SHA, instala Datadog no EKS, aplica a API, aguarda rollout, testa readiness público e aplica dashboards/monitores.

O Flyway executa durante o startup.

Acompanhe em **Actions → CI/CD - Oficina API** a execução disparada pelo merge. Para a [alternativa manual ou reexecução](#primeiro-deploy-e-reexecuções), selecione **Run workflow → homologacao**; a branch deve conter o código desejado e as dependências devem estar prontas. Para produção, selecione `main`. Pelo terminal, use `cicd.yml`:

```powershell
gh workflow run cicd.yml --repo tc-organization-26/gestao-oficina-tc-1 --ref homologacao
gh run list --repo tc-organization-26/gestao-oficina-tc-1 --workflow cicd.yml --limit 3
gh run watch ID_DA_EXECUCAO --repo tc-organization-26/gestao-oficina-tc-1 --exit-status
```

O dispatch é assíncrono. Use o ID retornado pela listagem para acompanhar a execução correta e aguarde sucesso antes da próxima etapa.

## Validar o deploy

Com credenciais AWS válidas no terminal:

```powershell
$cluster = aws ssm get-parameter --name /oficina/hom/eks/cluster-name --query Parameter.Value --output text
aws eks update-kubeconfig --name $cluster --region us-east-1
kubectl get deployment,service,pods,hpa -n oficina -o wide
kubectl rollout status deployment/oficina-api -n oficina --timeout=300s
kubectl logs deployment/oficina-api -n oficina --tail=200
```

Obtenha a URL real e teste readiness, login e uma rota protegida:

```powershell
$apiId = aws ssm get-parameter --name /oficina/hom/edge/http-api-id --query Parameter.Value --output text
$baseUrl = "https://$apiId.execute-api.us-east-1.amazonaws.com"
Invoke-RestMethod "$baseUrl/actuator/health/readiness"
$login = Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/login" `
  -ContentType application/json `
  -Body (@{ cpf = "003.619.370-40" } | ConvertTo-Json)
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "$baseUrl/clientes" -Headers $headers
```

O login AWS recebe o CPF fictício. Não publique o token nos logs.

### Swagger e diagnóstico

Swagger/OpenAPI sob o Gateway também exigem JWT; abrir a URL diretamente no navegador pode retornar rejeição de acesso. Para acesso administrativo à interface:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Abra `http://localhost:18081/swagger-ui/index.html`. O port-forward não passa pelo Gateway/Authorizer; chamadas de negócio continuam sujeitas ao Spring Security.

## Problemas comuns

Consulte o [troubleshooting deste repositório](TROUBLESHOOTING.md) para credenciais, branches, dependências e falhas do componente.

## Encerramento do ambiente

Remova os workloads da API antes das dependências. Os manifests estão em `k8s/cloud/`; não exclua um namespace compartilhado sem conferir os recursos existentes. A observabilidade mantém state próprio em `oficina/observability/ENV.tfstate`: remova os recursos Datadog com esse mesmo state e desinstale Agents/Operator antes do EKS. UptimeRobot é externo e precisa de remoção/atualização manual.

Ordem geral: API e observabilidade → autenticação → RDS → EKS/rede → bucket de state. Renovar credenciais não remove recursos. Para repetir uma sessão, confirme conta, região e state antes de executar novamente.

## Outros componentes

Consulte os [repositórios e suas responsabilidades](../README.md#repositórios-da-solução).
