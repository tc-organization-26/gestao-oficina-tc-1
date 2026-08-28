# Execução, Terraform e Deploy

Este documento concentra as instruções práticas para executar a Oficina API localmente, provisionar a infraestrutura e fazer deploy em Kubernetes.

## Tecnologias e versões usadas ou definidas no projeto

- Git.
- Docker Desktop.
- Java 21, apenas se for executar ou compilar fora do Docker.
- `kubectl`, para Kubernetes.
- Terraform `>= 1.6.0`, para provisionamento.

| Dependência | Versão / referência |
| --- | --- |
| Java / JDK | 21 |
| Eclipse Temurin JDK | 21 Alpine |
| Eclipse Temurin JRE | 21 Alpine |
| Maven Wrapper | 3.3.4 |
| Apache Maven usado pelo wrapper | 3.9.16 |
| Spring Boot | 4.0.7 |
| Spring Web MVC | 7.0.8, resolvido pelo BOM do Spring Boot |
| Spring Data JPA | 4.0.6, resolvido pelo BOM do Spring Boot |
| Spring Security Core | 7.0.6, resolvido pelo BOM do Spring Boot |
| Spring Validation Starter | 4.0.7, gerenciado pelo Spring Boot |
| Hibernate Validator | 9.0.1.Final, resolvido pelo BOM do Spring Boot |
| Spring Actuator Starter | 4.0.7, gerenciado pelo Spring Boot |
| Flyway Core | 11.14.1, resolvido pelo BOM do Spring Boot |
| Flyway PostgreSQL | 11.14.1, resolvido pelo BOM do Spring Boot |
| PostgreSQL JDBC Driver | 42.7.11, resolvido pelo BOM do Spring Boot |
| PostgreSQL Server | 16 Alpine |
| Springdoc OpenAPI / Swagger UI | 3.0.2 |
| JJWT | 0.12.6 |
| Lombok | 1.18.46, resolvido pelo BOM do Spring Boot |
| Build Helper Maven Plugin | 3.6.0 |
| JaCoCo Maven Plugin | 0.8.12 |
| Docker Compose | Compatível com Docker Compose v2 |
| Kubernetes | Compatível com API `apps/v1`, `v1` e `autoscaling/v2` |
| Terraform | `>= 1.6.0` |
| Provider Kubernetes Terraform | `~> 2.31`, lockado em `2.38.0` |
| GitHub Actions | Actions oficiais v3/v4/v6 |
| GitHub Container Registry | `ghcr.io` |
| Insomnia | Collection YAML |

Versões validadas no ambiente local usado durante a documentação:

| Ferramenta local | Versão validada |
| --- | --- |
| Java instalado na máquina | 21.0.11 |
| Docker Desktop | 4.77.0 |
| Docker Engine | 29.5.3 |
| Docker Compose | v5.1.4 |
| Terraform instalado | 1.15.9 |
| `kubectl` | v1.34.1 |
| Kustomize embutido no `kubectl` | v5.7.1 |
| Kubernetes local Docker Desktop | v1.34.3 |
| Git | 2.54.0.windows.1 |
| Insomnia | 13.1.0 |

Para conferir versões em outra máquina:

```bash
git --version
docker version
docker compose version
kubectl version --client
terraform version
```

## Variáveis da aplicação

Não é necessário editar `src/main/resources/application.properties` para informar senha, usuário, URL do banco ou segredo JWT.

A aplicação lê configurações por variáveis de ambiente, incluindo:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `SECURITY_JWT_SECRET`
- `SECURITY_JWT_EXPIRATION_SECONDS`

Use:

- `.env` para Docker Compose.
- `infra/terraform.tfvars` ou `TF_VAR_*` para Kubernetes com Terraform.
- Variáveis da própria máquina para execução sem Docker.

Os arquivos `.env` e `infra/terraform.tfvars` não devem ser versionados, pois podem conter senhas e segredos.

## Execução local com Docker Compose

Use esta opção para subir rapidamente a API e o PostgreSQL localmente.

### Passo a passo

Clone o repositório:

```bash
git clone https://github.com/tc-organization-26/gestao-oficina-tc-1.git
cd gestao-oficina-tc-1
```

Crie o arquivo `.env`:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
copy .env.example .env
```

Edite o `.env`:

```env
POSTGRES_DB=oficina_db_2
POSTGRES_USER=postgres
POSTGRES_PASSWORD=troque_aqui

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/oficina_db_2
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=troque_aqui

JWT_SECRET=troque_por_uma_chave_segura_com_32_bytes_ou_mais
JWT_EXPIRATION_SECONDS=3600
```

`POSTGRES_PASSWORD` e `SPRING_DATASOURCE_PASSWORD` devem ter o mesmo valor.

Suba os containers:

```bash
docker compose up --build
```

O Compose cria:

- Container `oficina-postgres`.
- Container `oficina-api`.
- Volume `oficina-postgres-data`.
- Rede interna para a API acessar o banco pelo host `postgres`.

A API fica disponível em:

```text
http://localhost:8081
```

O Swagger fica disponível em:

```text
http://localhost:8081/swagger-ui/index.html
```

Comandos úteis:

```bash
docker compose logs -f
docker compose down
docker compose down -v
```

`docker compose down -v` remove os dados locais do PostgreSQL.

## Build sem Docker

Para compilar com Maven Wrapper:

```powershell
.\mvnw.cmd clean package
```

Em Linux/macOS:

```bash
./mvnw clean package
```

Para executar fora do Docker, é necessário ter Java 21, PostgreSQL disponível e variáveis de ambiente configuradas.

## Deploy em Kubernetes com Terraform

Essa opção executa a API e o banco PostgreSQL dentro do cluster Kubernetes.

O Kubernetes recebe:

- Deployment da API.
- Deployment do PostgreSQL.
- Service interno `postgres`.
- Service `NodePort` da API.
- ConfigMaps.
- Secrets.
- PVC do PostgreSQL.
- HPA da API.

### Preparar Kubernetes local

No Docker Desktop:

1. Abra o Docker Desktop.
2. Acesse `Settings > Kubernetes`.
3. Marque `Enable Kubernetes`.
4. Clique em `Apply & Restart`.
5. Aguarde o Kubernetes ficar com status `Running`.

Depois confira o contexto:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

### Instalar Terraform no Windows

Com Winget:

```powershell
winget install HashiCorp.Terraform
```

Depois abra um novo terminal e confira:

```powershell
terraform version
```

Também é possível instalar manualmente pelo site da HashiCorp, extrair `terraform.exe` e adicionar a pasta ao `PATH`.

## Provisionamento com Terraform

Use esta sequência a partir da raiz do projeto:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
docker build -t oficina-api:local .
copy infra\terraform.tfvars.example infra\terraform.tfvars
notepad infra\terraform.tfvars
cd .\infra
terraform init -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
terraform plan
terraform apply
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Durante o `terraform apply`, digite `yes` quando aparecer a confirmação:

```text
Do you want to perform these actions?
  Enter a value:
```

Com o `port-forward` aberto, acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Exemplo de `infra/terraform.tfvars`:

```hcl
kubeconfig_context = "docker-desktop"
postgres_storage_class_name = "hostpath"

postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

No Minikube, o StorageClass costuma ser:

```hcl
postgres_storage_class_name = "standard"
```

Também é possível passar valores por variáveis de ambiente:

```powershell
$env:TF_VAR_kubeconfig_context = "docker-desktop"
$env:TF_VAR_postgres_storage_class_name = "hostpath"
$env:TF_VAR_postgres_user = "postgres"
$env:TF_VAR_postgres_password = "troque_aqui"
$env:TF_VAR_jwt_secret = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Para recriar a configuração local do backend Kubernetes:

```powershell
cd infra
terraform init -reconfigure -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

Para remover os recursos:

```powershell
cd infra
terraform destroy
```

## Acesso à API no Kubernetes

No Docker Desktop, a API pode ficar disponível pelo NodePort:

```text
http://localhost:30081/swagger-ui/index.html
```

O caminho mais previsível localmente é o `port-forward`:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Se a porta `18081` estiver ocupada:

```powershell
kubectl port-forward -n oficina service/oficina-api 18082:8081
```

Nesse caso, use:

```text
http://localhost:18082/swagger-ui/index.html
```

## Metrics Server e HPA

O HPA depende do Metrics Server. Se o cluster local não tiver esse componente, o HPA será criado, mas as métricas podem aparecer como indisponíveis.

Instalação opcional:

```powershell
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl rollout status deployment/metrics-server -n kube-system
kubectl top nodes
kubectl top pods -n oficina
```

No Docker Desktop, se houver erro de certificado do kubelet:

```powershell
kubectl patch deployment metrics-server -n kube-system --type=strategic --patch-file k8s/metrics-server-docker-desktop-patch.json
kubectl rollout status deployment/metrics-server -n kube-system
```

## Continuous Deployment com GitHub Actions

O workflow [`.github/workflows/cicd.yml`](../.github/workflows/cicd.yml) configura Continuous Deployment.

Fluxo esperado:

- Push na branch `fase-2`: executa build, testes e cria pull request para a branch principal quando ainda não existir.
- Pull request aberto ou sincronizado: não dispara o workflow novamente.
- Merge ou push em `main` ou `master`: executa build, testes, build da imagem Docker, publicação no GHCR e deploy no Kubernetes.

O job de deploy usa runner self-hosted Windows:

```yaml
runs-on: [self-hosted, Windows]
```

Se o GitHub Actions mostrar `Waiting for a runner to pick up this job`, o workflow está aguardando uma máquina Windows com o runner online e registrado no repositório ou organização.

### Configuração do runner self-hosted

No GitHub:

1. Acesse `Settings > Actions > Runners`.
2. Clique em `New self-hosted runner`.
3. Escolha `Windows` e arquitetura `x64`.
4. Siga os comandos exibidos pelo GitHub para baixar, extrair e configurar o runner.

Exemplo de instalação:

```powershell
mkdir actions-runner
cd actions-runner
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/v2.336.0/actions-runner-win-x64-2.336.0.zip -OutFile actions-runner-win-x64-2.336.0.zip
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$PWD/actions-runner-win-x64-2.336.0.zip", "$PWD")
.\config.cmd --url https://github.com/<organizacao-ou-usuario>/<repositorio> --token <TOKEN_GERADO_PELO_GITHUB>
.\run.cmd
```

A máquina do runner precisa ter:

```powershell
kubectl version --client
terraform version
docker version
```

Se o Terraform não estiver no `PATH`, configure a variable `TERRAFORM_EXE` no GitHub Actions. Exemplo:

```text
C:\Program Files\Terraform\terraform.exe
```

### Secrets e variables do GitHub Actions

Secrets necessários:

| Secret | Uso |
| --- | --- |
| `KUBE_CONFIG_BASE64` | Kubeconfig do cluster codificado em Base64 |
| `POSTGRES_USER` | Usuário do PostgreSQL |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Segredo para assinar tokens JWT |
| `GHCR_USERNAME` | Usuário do GHCR, se a imagem estiver privada |
| `GHCR_TOKEN` | Token de leitura do GHCR, se a imagem estiver privada |

Variables opcionais:

| Variable | Uso |
| --- | --- |
| `KUBE_CONTEXT` | Contexto Kubernetes do kubeconfig |
| `POSTGRES_STORAGE_CLASS_NAME` | StorageClass do PVC, padrão `hostpath` |
| `TERRAFORM_EXE` | Caminho do executável Terraform no runner |

Para gerar `KUBE_CONFIG_BASE64` no PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\.kube\config"))
```

O state do Terraform no CD usa backend Kubernetes, gravado como Secret no namespace `default`. O workflow não possui aprovação manual de ambiente; após push em `main` ou `master`, passando build e testes, o deploy segue automaticamente.

## Collection da API

Importe no Insomnia:

```text
src/main/resources/collection-insomnia.yaml
```

Base URLs recomendadas:

```text
Docker Compose: http://localhost:8081
Kubernetes com port-forward: http://localhost:18081
```

Para executar o fluxo principal, use `Run folder` na pasta `FLUXO COMPLETO`.
