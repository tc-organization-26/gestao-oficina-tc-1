# Oficina API no AWS Lab

Este repositório é o último passo da implantação. Ele constrói a aplicação, publica a imagem no GHCR e aplica os manifests no EKS criado pelos demais repositórios.

## Arquitetura completa

```text
Cliente
  -> API Gateway HTTP API
     -> POST /auth/login -> Lambda login -> JWT
     -> outras rotas -> Lambda Authorizer
        -> VPC Link
        -> NLB privado
        -> Service NodePort 30081
        -> Pods Spring Boot no EKS
        -> RDS PostgreSQL privado

GitHub Actions
  -> credenciais temporárias do AWS Lab
  -> SSM/Secrets Manager para descoberta e segredos
  -> kubectl no EKS
  -> imagem privada no GHCR
```

A aplicação segue Clean Architecture, usa Flyway para migrations e não armazena credenciais no repositório.

## Ordem obrigatória

Antes deste workflow, conclua:

```text
1. oficina-k8s-infra/bootstrap.yml
2. oficina-k8s-infra/terraform.yml
3. oficina-db-infra/terraform.yml
4. oficina-auth-lambda/cicd.yml
5. este repositório/cicd.yml
```

## Configuração GitHub

Organization secrets compartilhados com os quatro repositórios:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_SESSION_TOKEN
```

Secrets específicos da aplicação:

```text
GHCR_USERNAME
GHCR_TOKEN
```

`GHCR_TOKEN` precisa de `read:packages` para o cluster baixar a imagem privada. O workflow usa `GITHUB_TOKEN` para publicar a imagem.

Variables esperadas:

```text
AWS_REGION=us-east-1
TF_STATE_BUCKET=oficina-fiap-tfstate-ACCOUNT_ID
LAB_ROLE_ARN=arn:aws:iam::ACCOUNT_ID:role/LabRole
```

Este workflow não cria recursos IAM e não usa OIDC.

## Promoção automática entre branches

O workflow usa o `GITHUB_TOKEN` fornecido automaticamente pelo GitHub. Em **Settings → Actions → General → Workflow permissions**, selecione **Read and write permissions** e habilite **Allow GitHub Actions to create and approve pull requests**. Não é necessário cadastrar outro secret.

O fluxo de promoção é:

```text
push em fase-3
  -> valida e abre ou mantém PR de fase-3 para homologacao
  -> merge manual após os checks
  -> valida e faz deploy em homologacao
  -> após sucesso, abre ou mantém PR de homologacao para main na mesma execução
  -> merge manual após os checks
```

Se `homologacao` ainda não existir, o workflow a cria a partir de `main`. Proteja `homologacao` e `main` contra push direto e exija Pull Request para merge.

## Teste local opcional

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Endereços:

```text
API:     http://localhost:8081
Swagger: http://localhost:8081/swagger-ui/index.html
```

Para executar apenas os testes:

```powershell
./mvnw.cmd clean verify
```

## Deploy no Lab

Pelo GitHub: **Actions → CI/CD - Oficina API → Run workflow**, selecionando `fase-3`.

Pelo terminal:

```powershell
gh workflow run cicd.yml `
  --repo tc-organization-26/gestao-oficina-tc-1 `
  --ref fase-3

gh run list `
  --repo tc-organization-26/gestao-oficina-tc-1 `
  --workflow cicd.yml `
  --limit 3

gh run watch ID `
  --repo tc-organization-26/gestao-oficina-tc-1 `
  --exit-status
```

O pipeline:

1. sobe PostgreSQL temporário e executa os testes Maven;
2. constrói e publica a imagem no GHCR;
3. consulta cluster, RDS e secrets pelo SSM/Secrets Manager;
4. cria ConfigMap e Secrets Kubernetes;
5. aplica Deployment, Service e HPA;
6. aguarda o rollout;
7. publica a URL do API Gateway no resumo e no deployment do GitHub.

## Validação Kubernetes

```powershell
$cluster = aws ssm get-parameter `
  --name /oficina/hom/eks/cluster-name `
  --query Parameter.Value `
  --output text

aws eks update-kubeconfig --name $cluster --region us-east-1
kubectl get deployment,service,pods,hpa -n oficina -o wide
kubectl rollout status deployment/oficina-api -n oficina --timeout=300s
kubectl logs deployment/oficina-api -n oficina --tail=200
```

## Teste da API

```powershell
$apiId = aws ssm get-parameter `
  --name /oficina/hom/edge/http-api-id `
  --query Parameter.Value `
  --output text

$baseUrl = "https://$apiId.execute-api.us-east-1.amazonaws.com"

$login = Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/auth/login" `
  -ContentType application/json `
  -Body (@{ cpf = "003.619.370-40" } | ConvertTo-Json)

$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "$baseUrl/clientes" -Headers $headers |
  ConvertTo-Json -Depth 10
```

Swagger publicado:

```text
BASE_URL/swagger-ui/index.html
```

Quando a sessão do Lab expirar, atualize os três Organization secrets antes de reexecutar qualquer deploy.
