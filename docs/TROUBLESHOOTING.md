# Troubleshooting

Este documento reúne problemas comuns de execução local, Kubernetes, Terraform e API.

## Terraform não reconhecido

Erro:

```text
terraform : O termo 'terraform' não é reconhecido
```

Instale o Terraform ou adicione a pasta do `terraform.exe` ao `PATH`. Depois abra um novo terminal e rode:

```powershell
terraform version
```

## `Invalid character` no Terraform

Erro:

```text
Error: Invalid character
postgres_user = ${SPRING_DATASOURCE_USERNAME:postgres}
```

O arquivo `terraform.tfvars` está usando sintaxe do Spring. Use valores diretos:

```hcl
postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Ou use variáveis `TF_VAR_*`.

## Terraform executado na pasta errada

Execute `terraform init`, `terraform plan`, `terraform apply` e `terraform destroy` dentro de `infra`:

```powershell
cd infra
dir
```

A pasta deve conter `main.tf`, `providers.tf`, `variables.tf`, `outputs.tf` e `terraform.tfvars`.

## Terraform não baixa provider

Se `terraform init` falhar com download do provider:

```powershell
terraform init -upgrade
```

Verifique internet, VPN ou proxy.

## Terraform pede `secret_suffix`

Se o Terraform pedir `secret_suffix` ou informar backend não inicializado, reconfigure o backend:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
cd infra
terraform init -reconfigure -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

## Kubernetes sem contexto

Erro:

```text
error: current-context is not set
```

No Docker Desktop:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

Se `docker-desktop` não aparecer, habilite o Kubernetes no Docker Desktop.

## PVC do PostgreSQL travado

Se o Terraform ficar criando o PVC por muito tempo, confira os StorageClasses:

```powershell
kubectl get storageclass
```

No Docker Desktop, normalmente use:

```hcl
postgres_storage_class_name = "hostpath"
```

No Minikube, normalmente use:

```hcl
postgres_storage_class_name = "standard"
```

Para investigar:

```powershell
kubectl get pvc -n oficina
kubectl describe pvc postgres-data -n oficina
```

## Imagem local não encontrada no Kubernetes

Se o pod da API ficar em `ImagePullBackOff` ou `ErrImageNeverPull`, gere a imagem antes do `terraform apply`:

```powershell
docker build -t oficina-api:local .
cd infra
terraform apply
```

## NodePort não abre

Se `http://localhost:30081` não abrir, use `port-forward`:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Depois acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Para confirmar o Service:

```powershell
kubectl get endpoints -n oficina
kubectl describe service oficina-api -n oficina
```

## Docker Desktop não está rodando

Erro:

```text
failed to connect to the docker API
```

Abra o Docker Desktop, aguarde a inicialização e teste:

```powershell
docker version
```

## Docker Compose não reconhecido

Se `docker compose` não existir, verifique se o Docker Desktop está atualizado. Em instalações antigas, tente:

```bash
docker-compose up --build
```

## `.env` não encontrado

Crie o arquivo a partir do exemplo:

```powershell
copy .env.example .env
```

Em Bash:

```bash
cp .env.example .env
```

Depois preencha senhas e segredo JWT.

## Senha do banco diferente da senha da aplicação

Se aparecer autenticação recusada no PostgreSQL, confira se os valores são iguais:

```env
POSTGRES_PASSWORD=troque_aqui
SPRING_DATASOURCE_PASSWORD=troque_aqui
```

Depois reinicie:

```bash
docker compose down
docker compose up --build
```

## Banco com credenciais antigas

Se você alterou usuário, senha ou nome do banco após a primeira execução, o volume pode manter as credenciais antigas.

Para recriar o banco local:

```bash
docker compose down -v
docker compose up --build
```

Esse comando apaga os dados locais.

## Porta 5432 ou 8081 em uso

No `docker-compose.yml`, o PostgreSQL local está publicado em `5433:5432`. Se precisar alterar, ajuste o lado esquerdo do mapeamento.

Para mudar a porta da API:

```yaml
ports:
  - "8082:8081"
```

Depois acesse:

```text
http://localhost:8082/swagger-ui/index.html
```

## Aplicação inicia antes do banco

Se aparecer conexão recusada com o PostgreSQL, aguarde alguns segundos e rode novamente:

```bash
docker compose up --build
```

Para acompanhar:

```bash
docker compose logs -f
```

## Erro nas migrations Flyway

Em ambiente local, se o banco estiver com schema antigo, recrie o volume:

```bash
docker compose down -v
docker compose up --build
```

Esse comando apaga os dados locais.

## JWT secret curto

Use uma chave com pelo menos 32 caracteres:

```env
JWT_SECRET=minha_chave_super_secreta_com_mais_de_32_caracteres
```

## Endpoints retornam 401

Alguns endpoints exigem autenticação.

Faça login:

```text
POST /auth/login
```

Envie o token:

```text
Authorization: Bearer seu_token_aqui
```

No Swagger, clique em `Authorize` e informe o token.

## Build Maven não baixa dependências

Verifique internet, proxy ou VPN. Com Docker:

```bash
docker compose build --no-cache
docker compose up
```

## Permissão negada no Maven Wrapper

Em Linux/macOS:

```bash
chmod +x mvnw
./mvnw clean package
```

## Java incorreto

O projeto exige Java 21. Confira:

```bash
java -version
```

No Windows, verifique também se `JAVA_HOME` aponta para um JDK 21.

## Swagger não abre

Confira os containers:

```bash
docker compose ps
docker compose logs -f app
```

Se mudou a porta da API, use a nova porta na URL do Swagger.

