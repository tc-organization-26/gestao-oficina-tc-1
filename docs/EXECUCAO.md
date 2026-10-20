# Execução e deploy da aplicação

[Voltar ao README principal](../README.md)

Este repositório contém a aplicação Spring Boot, sua imagem Docker, o ambiente local com Docker Compose e os manifests usados para implantar a API no EKS. A infraestrutura AWS fica nos três repositórios irmãos.

## Execução local

Pré-requisitos: Docker Desktop e Docker Compose v2.

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Depois acesse:

- Swagger: `http://localhost:8081/swagger-ui/index.html`
- API: `http://localhost:8081`

O Docker Compose cria um PostgreSQL local apenas para desenvolvimento. O banco da nuvem é um serviço gerenciado separado e não usa PVC do Kubernetes.

Para encerrar:

```powershell
docker compose down
```

Para apagar também o volume local do Compose:

```powershell
docker compose down --volumes
```

## Build e testes

Com Java 21:

```powershell
./mvnw.cmd clean verify
```

Com Docker:

```powershell
docker build -t oficina-api:local .
```

## Repositórios do ambiente

| Repositório | Responsabilidade |
| --- | --- |
| `gestao-oficina-tc-1` | Aplicação, imagem no GHCR e deploy no EKS |
| `oficina-k8s-infra` | Bootstrap, VPC, EKS, escalabilidade e API Gateway |
| `oficina-db-infra` | Banco PostgreSQL gerenciado e Secrets Manager |
| `oficina-auth-lambda` | Function Serverless de autenticação |

## Ordem da primeira implantação

1. Crie os quatro repositórios vazios no GitHub e envie o conteúdo de cada pasta local.
2. No `oficina-k8s-infra`, configure os secrets iniciais e execute o workflow `Bootstrap AWS e GitHub`.
3. Execute o CI/CD de `oficina-k8s-infra` para criar a rede, o EKS, a escalabilidade e o API Gateway.
4. Execute o CI/CD de `oficina-db-infra` para criar o PostgreSQL gerenciado.
5. Execute o CI/CD de `oficina-auth-lambda` para publicar a Lambda.
6. Faça push da aplicação para publicar a imagem no GHCR e implantá-la no EKS.

Nas execuções seguintes, cada repositório aplica apenas suas próprias mudanças. O bootstrap consulta o state remoto e não recria os pré-requisitos que já existem.

## GitHub Actions da aplicação

O workflow `.github/workflows/cicd.yml` executa testes, publica a imagem em `ghcr.io` e atualiza o Deployment no EKS.

Variables configuradas automaticamente pelo bootstrap:

- `AWS_ROLE_ARN`
- `AWS_REGION`
- `TF_STATE_BUCKET`

Secrets específicos da aplicação:

- `GHCR_USERNAME`: usuário que possui acesso de leitura ao pacote no GHCR;
- `GHCR_TOKEN`: token com permissão `read:packages`, necessário quando a imagem é privada.

O workflow lê do AWS Systems Manager Parameter Store o nome do cluster e os dados de conexão do banco. Senhas e o segredo JWT são obtidos do AWS Secrets Manager e criados como Secret no namespace `oficina` durante o deploy.

## Ambientes

- Push em `homologacao`: usa os parâmetros sob `/oficina/hom` e o environment `homologacao`.
- Push em `main` ou `master`: usa os parâmetros sob `/oficina/prod` e o environment `producao`.

## Validação

Após o deploy:

```powershell
kubectl get deployment,service,hpa -n oficina
kubectl rollout status deployment/oficina-api -n oficina --timeout=300s
```

O acesso externo normal ocorre pelo API Gateway criado no repositório de Kubernetes. Para uma verificação administrativa direta:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Abra `http://localhost:18081/swagger-ui/index.html`.

## Tecnologias principais

- Java 21 e Spring Boot
- PostgreSQL 16 para desenvolvimento local
- Docker e GitHub Container Registry
- Amazon EKS, RDS, Lambda e API Gateway
- Terraform e GitHub Actions
