# Execução, Terraform e Deploy

[Voltar ao README principal](../README.md)

Este documento concentra as instruções práticas para executar a Oficina API localmente, provisionar a infraestrutura e fazer deploy em Kubernetes.

## Opções de execução local

A aplicação pode ser executada localmente de duas formas:

- Docker Compose: caminho mais rápido para subir API e PostgreSQL.
- Kubernetes local com Terraform: caminho mais próximo do deploy em cluster, usando Docker Desktop Kubernetes.

### Opção 1: Docker Compose

Use a partir da raiz do projeto:

```bash
cp .env.example .env
# edite o .env e defina POSTGRES_PASSWORD e JWT_SECRET
docker compose up --build
```

No Windows PowerShell:

```powershell
copy .env.example .env
# edite o .env e defina POSTGRES_PASSWORD e JWT_SECRET
docker compose up --build
```

Depois acesse:

```text
Swagger: http://localhost:8081/swagger-ui/index.html
API base URL: http://localhost:8081
```

Como alternativa ao Swagger, importe a collection [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) no Insomnia e ajuste apenas a base URL para `http://localhost:8081`.

### Opção 2: Kubernetes local com Terraform

Pré-requisitos:

- Docker Desktop com Kubernetes habilitado.
- `kubectl` apontando para o contexto `docker-desktop`.
- Terraform instalado.

[Informações sobre versões](#tecnologias-e-versões-usadas-ou-definidas-no-projeto)

Use a partir da raiz do projeto:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
docker build -t oficina-api:local .
copy infra\terraform.tfvars.example infra\terraform.tfvars
# edite infra\terraform.tfvars e defina postgres_user, postgres_password e jwt_secret
cd .\infra
terraform init -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
terraform apply
kubectl get all -n oficina
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

Durante o `terraform apply`, confirme digitando `yes`.

Com o `port-forward` aberto:

```text
Swagger: http://localhost:18081/swagger-ui/index.html
API base URL: http://localhost:18081
```

Como alternativa ao Swagger, importe a collection [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) no Insomnia e ajuste apenas a base URL para `http://localhost:18081`.

Para remover o ambiente Kubernetes local:

```powershell
cd infra
terraform destroy
```

Para conferir o ambiente Kubernetes local:

```powershell
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
```

Credencial administrativa fictícia para gerar token JWT em `POST /auth/login`:

```json
{
  "login": "admin",
  "senha": "ad@456"
}
```

As credenciais do `.env` e de `infra/terraform.tfvars` configuram infraestrutura da aplicação, como senha do PostgreSQL e segredo JWT. Elas podem ser definidas por quem estiver executando o projeto.

O login da API é separado dessas variáveis. Neste MVP, o usuário administrativo fictício é fixo na aplicação: `admin` / `ad@456`. Outros logins não autenticam enquanto não houver cadastro ou persistência real de usuários.

Os detalhes, comandos de diagnóstico e alternativas de execução aparecem nas seções abaixo.

## Tecnologias e versões usadas ou definidas no projeto

- Git.
- Docker Desktop.
- Java 21, apenas se for executar ou compilar fora do Docker.
- `kubectl`, para Kubernetes.
- AWS CLI, para autenticar na AWS, ECR e EKS.
- `eksctl`, para criar e remover cluster Amazon EKS.
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
| Amazon EKS | Kubernetes gerenciado na AWS |
| Amazon ECR | Registry Docker privado da AWS |
| AWS CLI | v2 recomendada |
| `eksctl` | 0.230.0 validado no laboratório |
| Insomnia | Collection YAML |

Versões validadas no ambiente local usado durante a documentação:

| Ferramenta local | Versão validada |
| --- | --- |
| Sistema operacional | Windows 11 |
| Java instalado na máquina | 21.0.11 |
| Docker Desktop | 4.77.0 |
| Docker Engine | 29.5.3 |
| Docker Compose | v5.1.4 |
| Terraform instalado | 1.15.9 |
| `kubectl` | v1.34.1 |
| Kustomize embutido no `kubectl` | v5.7.1 |
| Kubernetes local Docker Desktop | v1.34.3 |
| AWS CLI local | aws-cli/2.36.34 Python/3.14.6 Windows/11 |
| Amazon EKS no laboratório | Kubernetes 1.34 |
| Região AWS validada | us-east-1 |
| `kubectl` instalado no AWS Academy | 1.34.9 |
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
- `POSTGRES_PASSWORD`
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

JWT_SECRET=troque_por_uma_chave_segura_com_32_bytes_ou_mais
JWT_EXPIRATION_SECONDS=3600
```

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

Também é possível validar pelo Insomnia importando [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) e ajustando apenas a base URL para `http://localhost:8081`.

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

Também é possível validar pelo Insomnia importando [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) e ajustando apenas a base URL para `http://localhost:18081`.

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

No Insomnia, importe [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) e ajuste apenas a base URL para `http://localhost:30081`.

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

No Insomnia, ajuste apenas a base URL da collection para `http://localhost:18082`.

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

O job de deploy usa runner GitHub-hosted Ubuntu:

```yaml
runs-on: ubuntu-latest
```

O workflow instala/configura `kubectl` e Terraform durante a execução e usa os secrets AWS para autenticar no EKS. Portanto, não é necessário manter uma máquina local com runner self-hosted ligada.

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
| `POSTGRES_STORAGE_CLASS_NAME` | StorageClass do PVC. No CD para EKS, padrão `gp2` |
| `APP_SERVICE_TYPE` | Tipo do Service da API. No CD para EKS, padrão `LoadBalancer` |

Para gerar `KUBE_CONFIG_BASE64` no PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\.kube\config"))
```

O state do Terraform no CD usa backend Kubernetes, gravado como Secret no namespace `default`. O workflow não possui aprovação manual de ambiente; após push em `main` ou `master`, passando build e testes, o deploy segue automaticamente.

No fluxo atual de CD, a aplicação roda no Amazon EKS e a imagem é publicada no GitHub Container Registry (`ghcr.io`). O workflow passa `app_service_type = "LoadBalancer"` para o Terraform, então a AWS cria uma URL pública para acessar a API na porta `8081`.

### Configurar GitHub Actions para EKS com imagem no GHCR

Para manter o cluster na AWS e a imagem no GitHub Container Registry, configure o GitHub em `Settings > Secrets and variables > Actions`.

Secrets necessários:

| Secret | Valor esperado |
| --- | --- |
| `KUBE_CONFIG_BASE64` | Conteúdo do kubeconfig do EKS codificado em Base64 |
| `POSTGRES_USER` | Usuário do PostgreSQL da aplicação |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL da aplicação |
| `JWT_SECRET` | Chave JWT com 32 bytes ou mais |
| `GHCR_USERNAME` | Usuário do GitHub que consegue ler a imagem no GHCR |
| `GHCR_TOKEN` | Token do GitHub com permissão de leitura de packages |
| `AWS_ACCESS_KEY_ID` | Access key da AWS usada para autenticar no EKS |
| `AWS_SECRET_ACCESS_KEY` | Secret key da AWS usada para autenticar no EKS |
| `AWS_SESSION_TOKEN` | Session token da AWS Academy, quando estiver usando o laboratório |

Variables recomendadas:

| Variable | Valor recomendado para EKS |
| --- | --- |
| `AWS_REGION` | Região do cluster, por exemplo `us-east-1` |
| `KUBE_CONTEXT` | Contexto do EKS, por exemplo `arn:aws:eks:us-east-1:ACCOUNT_ID:cluster/oficina-cluster` |
| `POSTGRES_STORAGE_CLASS_NAME` | `gp2` ou `gp3`, conforme `kubectl get storageclass` |
| `APP_SERVICE_TYPE` | `LoadBalancer` |

O workflow já define `AWS_REGION` como `us-east-1`, `APP_SERVICE_TYPE` como `LoadBalancer` e `POSTGRES_STORAGE_CLASS_NAME` como `gp2` quando essas variables não existem. Mesmo assim, cadastrar as variables no GitHub deixa a configuração explícita para apresentação e manutenção.

O kubeconfig do EKS usa autenticação via AWS CLI. Por isso, os secrets AWS acima precisam estar atualizados. No AWS Academy, as credenciais mudam a cada sessão do laboratório.

## Collection da API

Importe no Insomnia a collection [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml).

Base URLs recomendadas:

```text
Docker Compose: http://localhost:8081
Kubernetes com port-forward: http://localhost:18081
Kubernetes com NodePort: http://localhost:30081
```

Para executar o fluxo principal, use `Run folder` na pasta `FLUXO COMPLETO`.

## Deploy em cloud AWS Academy com EKS e ECR

Este fluxo foi validado em laboratório AWS Academy usando Amazon EKS para executar o cluster Kubernetes e Amazon ECR para armazenar a imagem Docker da API.

O fluxo completo é:

```text
AWS Academy Start Lab
  -> criar cluster EKS com eksctl
  -> conferir nodes Ready com kubectl
  -> criar repositório no ECR
  -> fazer build Docker local
  -> publicar imagem no ECR
  -> aplicar recursos Kubernetes
  -> expor a API com LoadBalancer
  -> validar chamada HTTP, Swagger ou Insomnia
  -> remover tudo ao final do laboratório
```

### Diferença entre AWS Academy e conta pessoal

No AWS Academy, a conta é temporária e pode ter restrições de permissão. No laboratório validado, o usuário não podia criar novas IAM Roles com `iam:CreateRole`, então o cluster precisou usar a role já existente `LabRole`.

Em uma conta pessoal AWS, o usuário administrador normalmente pode deixar o `eksctl` criar as roles automaticamente. Também é possível configurar roles específicas com boas práticas de IAM, mas isso exige mais preparação.

Resumo:

| Ambiente | O que muda |
| --- | --- |
| AWS Academy | Usa credenciais temporárias, pode exigir `LabRole`, pode bloquear `sudo docker`, tem tempo limitado e orçamento do lab |
| Conta pessoal AWS | Usa credenciais permanentes ou SSO, cobra recursos reais, permite criar IAM Roles se o usuário tiver permissão |

### Preparar uma conta pessoal AWS

Em uma conta pessoal, antes de criar o EKS:

1. Configure MFA no usuário ou use AWS IAM Identity Center/SSO.
2. Defina uma região para todo o exercício, por exemplo `us-east-1`.
3. Crie um alerta de orçamento no AWS Billing para evitar surpresa de custo.
4. Use um usuário ou role com permissão para EKS, EC2, CloudFormation, IAM, ECR e Elastic Load Balancing.
5. Instale localmente AWS CLI, Docker, `kubectl` e `eksctl`.

Configure a AWS CLI:

```powershell
aws configure
```

Valide:

```powershell
aws sts get-caller-identity
aws configure get region
```

Em conta pessoal, o `eksctl` cria stacks CloudFormation que, por sua vez, criam VPC, subnets, security groups, IAM Roles, cluster EKS e nodegroup. Por isso, permissões parciais podem gerar falhas durante a criação.

Para reduzir custo durante testes:

- Use `desiredCapacity: 1` se a demonstração não exigir dois nodes.
- Delete o cluster assim que terminar.
- Delete o repositório ECR se não precisar manter a imagem.
- Confira se não sobrou Load Balancer.
- Evite deixar NAT Gateway e EC2 rodando fora do horário de teste.

### Recursos AWS usados

Durante o deploy em cloud, foram usados:

- Amazon EKS: cluster Kubernetes gerenciado.
- EC2 Managed Nodes: instâncias EC2 criadas pelo nodegroup do EKS.
- CloudFormation: stacks criadas pelo `eksctl`.
- IAM Role: role de serviço e nodes; no lab foi usada a `LabRole`.
- VPC, subnets, security groups, internet gateway e NAT: rede criada pelo `eksctl`.
- Amazon ECR: repositório privado `oficina-api` para a imagem Docker.
- Elastic Load Balancing: criado automaticamente pelo Service Kubernetes do tipo `LoadBalancer`.
- Kubernetes Namespace, Deployments, Services, ConfigMaps e Secrets.

### Preparar ferramentas no terminal do AWS Academy

No laboratório, clique em `Start Lab`, aguarde a bolinha `AWS` ficar verde, abra `AWS Details` e confirme que o terminal tem credenciais AWS ativas:

```bash
aws sts get-caller-identity
```

Instale `kubectl` no usuário do lab:

```bash
mkdir -p $HOME/bin
export PATH=$HOME/bin:$PATH
echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc

curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.34.9/2026-07-05/bin/linux/amd64/kubectl
chmod +x ./kubectl
mv ./kubectl $HOME/bin/kubectl

kubectl version --client
```

Instale `eksctl`:

```bash
ARCH=amd64
PLATFORM=$(uname -s)_$ARCH
curl -sLO "https://github.com/eksctl-io/eksctl/releases/latest/download/eksctl_$PLATFORM.tar.gz"
tar -xzf eksctl_$PLATFORM.tar.gz -C /tmp
mv /tmp/eksctl $HOME/bin/eksctl
chmod +x $HOME/bin/eksctl
rm eksctl_$PLATFORM.tar.gz

eksctl version
```

Se o `kubectl get nodes` falhar depois com erro de `ExecCredential` em `v1alpha1`, instale a AWS CLI v2 no usuário do lab:

```bash
cd ~
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -oq awscliv2.zip
./aws/install -i $HOME/aws-cli -b $HOME/bin --update
export PATH=$HOME/bin:$PATH

aws --version
```

### Criar cluster EKS no AWS Academy

Primeiro descubra a role disponível no lab:

```bash
aws iam list-roles --query "Roles[?contains(RoleName, 'LabRole')].[RoleName,Arn]" --output table
```

Crie `cluster.yaml`:

```bash
nano cluster.yaml
```

Conteúdo do arquivo, substituindo `ACCOUNT_ID` pelo ID da conta do lab:

```yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: oficina-cluster
  region: us-east-1
  version: "1.34"

iam:
  serviceRoleARN: arn:aws:iam::ACCOUNT_ID:role/LabRole

managedNodeGroups:
  - name: oficina-ng
    instanceType: t3.medium
    desiredCapacity: 2
    minSize: 1
    maxSize: 2
    iam:
      instanceRoleARN: arn:aws:iam::ACCOUNT_ID:role/LabRole
```

Crie o cluster:

```bash
eksctl create cluster -f cluster.yaml
```

Configure o kubeconfig:

```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name oficina-cluster
```

Valide:

```bash
kubectl get nodes
kubectl get pods -A
```

Os nodes devem aparecer com status `Ready`.

### Criar cluster EKS em conta pessoal AWS

Em uma conta pessoal com permissão administrativa, é possível criar o cluster sem informar `LabRole`:

```bash
eksctl create cluster \
  --name oficina-cluster \
  --region us-east-1 \
  --nodes 2 \
  --node-type t3.medium \
  --managed
```

Depois:

```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name oficina-cluster

kubectl get nodes
```

Atenção: em conta pessoal, EKS, EC2, NAT Gateway, EBS e Load Balancer podem gerar cobrança real enquanto existirem.

Se quiser um cluster mais econômico para uma validação rápida, use apenas um node:

```bash
eksctl create cluster \
  --name oficina-cluster \
  --region us-east-1 \
  --nodes 1 \
  --node-type t3.medium \
  --managed
```

Para um ambiente de demonstração com maior disponibilidade, mantenha dois nodes, como no exemplo anterior.

### Criar ECR e publicar a imagem

Crie o repositório:

```bash
aws ecr create-repository \
  --repository-name oficina-api \
  --region us-east-1
```

Pegue o ID da conta:

```bash
aws sts get-caller-identity --query Account --output text
```

No computador local, configure as credenciais da AWS. No AWS Academy, copie `AWS Access Key ID`, `AWS Secret Access Key` e `AWS Session Token` em `AWS Details`:

```powershell
aws configure set aws_access_key_id "ACCESS_KEY"
aws configure set aws_secret_access_key "SECRET_KEY"
aws configure set aws_session_token "SESSION_TOKEN"
aws configure set region "us-east-1"
```

Em conta pessoal, use as credenciais do seu usuário ou perfil AWS:

```powershell
aws configure
```

Confirme a conta:

```powershell
aws sts get-caller-identity
```

Entre na raiz do projeto e publique a imagem:

```powershell
cd [pasta raiz do seu projeto]

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

docker build -t oficina-api .
docker tag oficina-api:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
```

No laboratório validado, o terminal do AWS Academy não tinha permissão para acessar o Docker daemon com o usuário padrão. Por isso, o build e push foram feitos no computador local usando as credenciais temporárias do lab.

### Deploy manual no EKS com kubectl

Defina a imagem no terminal do lab ou no terminal que aponta para o EKS:

```bash
export IMAGE=ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
```

Crie namespace, Secrets e ConfigMaps:

```bash
kubectl create namespace oficina

kubectl create secret generic postgres-secret \
  -n oficina \
  --from-literal=POSTGRES_USER=postgres \
  --from-literal=POSTGRES_PASSWORD=troque_aqui

kubectl create configmap postgres-config \
  -n oficina \
  --from-literal=POSTGRES_DB=oficina_db_2 \
  --from-literal=PGDATA=/var/lib/postgresql/data/pgdata

kubectl create secret generic oficina-api-secret \
  -n oficina \
  --from-literal=SPRING_DATASOURCE_USERNAME=postgres \
  --from-literal=POSTGRES_PASSWORD=troque_aqui \
  --from-literal=SECURITY_JWT_SECRET=troque_por_uma_chave_segura_com_32_bytes_ou_mais

kubectl create configmap oficina-api-config \
  -n oficina \
  --from-literal=SERVER_PORT=8081 \
  --from-literal=SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/oficina_db_2 \
  --from-literal=SPRING_JPA_HIBERNATE_DDL_AUTO=none \
  --from-literal=SPRING_FLYWAY_BASELINE_ON_MIGRATE=true \
  --from-literal=SPRING_FLYWAY_BASELINE_VERSION=0 \
  --from-literal=SPRING_FLYWAY_VALIDATE_ON_MIGRATE=true \
  --from-literal=SECURITY_JWT_EXPIRATION_SECONDS=3600
```

Suba o PostgreSQL. Para demonstração no AWS Academy, foi usado `emptyDir`, que simplifica o lab e evita travas de PVC/EBS. Essa opção não persiste dados se o pod for recriado:

```bash
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: oficina
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:16-alpine
          ports:
            - containerPort: 5432
          envFrom:
            - configMapRef:
                name: postgres-config
            - secretRef:
                name: postgres-secret
          volumeMounts:
            - name: postgres-data
              mountPath: /var/lib/postgresql/data
      volumes:
        - name: postgres-data
          emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: oficina
spec:
  type: ClusterIP
  selector:
    app: postgres
  ports:
    - port: 5432
      targetPort: 5432
EOF
```

Suba a API:

```bash
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oficina-api
  namespace: oficina
spec:
  replicas: 1
  selector:
    matchLabels:
      app: oficina-api
  template:
    metadata:
      labels:
        app: oficina-api
    spec:
      containers:
        - name: oficina-api
          image: $IMAGE
          ports:
            - containerPort: 8081
          envFrom:
            - configMapRef:
                name: oficina-api-config
            - secretRef:
                name: oficina-api-secret
---
apiVersion: v1
kind: Service
metadata:
  name: oficina-api
  namespace: oficina
spec:
  type: LoadBalancer
  selector:
    app: oficina-api
  ports:
    - port: 8081
      targetPort: 8081
EOF
```

Acompanhe:

```bash
kubectl get pods -n oficina
kubectl logs deployment/oficina-api -n oficina --tail=50
kubectl get service oficina-api -n oficina
```

Quando o Service mostrar um endereço externo, acesse:

```text
http://ENDERECO_DA_AWS:8081/swagger-ui/index.html
```

Também é possível validar pelo Insomnia importando [collection-insomnia.yaml](../src/main/resources/collection-insomnia.yaml) e ajustando apenas a base URL para `http://ENDERECO_DA_AWS:8081`.

Também é possível testar com `port-forward`:

```bash
kubectl port-forward service/oficina-api 18081:8081 -n oficina
```

```text
http://localhost:18081/swagger-ui/index.html
```

No Insomnia, ajuste apenas a base URL da collection para `http://localhost:18081`.

### Deploy no EKS usando Terraform

O Terraform deste projeto já cria os recursos Kubernetes principais. Para usar em EKS, publique a imagem no ECR e ajuste `infra/terraform.tfvars`:

```hcl
kubeconfig_context = "arn:aws:eks:us-east-1:ACCOUNT_ID:cluster/oficina-cluster"
postgres_storage_class_name = "gp2"
app_service_type = "LoadBalancer"

app_image = "ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest"

postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Confira o StorageClass disponível:

```bash
kubectl get storageclass
```

Em EKS, use normalmente `gp2` ou `gp3`. Para PVC em EBS, pode ser necessário instalar o add-on:

```bash
eksctl create addon \
  --cluster oficina-cluster \
  --name aws-ebs-csi-driver \
  --region us-east-1
```

Se o `eksctl` não estiver disponível, use a AWS CLI:

```bash
aws eks create-addon \
  --cluster-name oficina-cluster \
  --addon-name aws-ebs-csi-driver \
  --region us-east-1
```

Para conferir:

```bash
aws eks describe-addon \
  --cluster-name oficina-cluster \
  --addon-name aws-ebs-csi-driver \
  --region us-east-1
```

Execute o Terraform:

```bash
cd infra
terraform init -reconfigure \
  -backend-config="config_path=$HOME/.kube/config" \
  -backend-config="namespace=default" \
  -backend-config="secret_suffix=oficina-api-aws"

terraform plan
terraform apply
```

Para exposição pública na AWS, o Service da API precisa ser `LoadBalancer`. No Terraform deste projeto, configure `app_service_type = "LoadBalancer"` ou, no GitHub Actions, use a variable `APP_SERVICE_TYPE` com valor `LoadBalancer`.

No CD automatizado deste repositório, a imagem publicada pelo GitHub Actions fica no GHCR. Nesse caso, não é necessário usar ECR: o Terraform recebe a imagem `ghcr.io/OWNER/REPOSITORY:SHA` diretamente do workflow e cria o `imagePullSecret` `ghcr-credentials` quando `GHCR_USERNAME` e `GHCR_TOKEN` estão configurados.

### Evidências recomendadas para entrega

Capture prints ou logs dos comandos:

```bash
kubectl get nodes
kubectl get pods -n oficina
kubectl get service oficina-api -n oficina
kubectl logs deployment/oficina-api -n oficina --tail=50
```

Também registre:

- URL pública ou `port-forward` usado.
- Swagger aberto ou collection do Insomnia executada com a base URL do ambiente.
- Chamada de API funcionando.
- Repositório ECR com a imagem publicada.
- Cluster EKS criado e depois removido.

### Limpeza para evitar custos

Antes de sair do AWS Academy ou de encerrar testes em conta pessoal, remova os recursos:

```bash
kubectl delete namespace oficina --ignore-not-found=true
```

Delete o cluster:

```bash
eksctl delete cluster \
  --name oficina-cluster \
  --region us-east-1 \
  --wait \
  --disable-nodegroup-eviction
```

Delete o ECR:

```bash
aws ecr delete-repository \
  --repository-name oficina-api \
  --region us-east-1 \
  --force
```

Confira se não sobrou nada:

```bash
aws eks list-clusters --region us-east-1

aws ec2 describe-instances \
  --region us-east-1 \
  --query "Reservations[].Instances[].[InstanceId,State.Name,Tags[?Key=='Name'].Value|[0]]" \
  --output table

aws elbv2 describe-load-balancers \
  --region us-east-1

aws ecr describe-repositories \
  --region us-east-1
```

Resultado esperado:

```text
EKS: nenhum cluster
EC2: instâncias do nodegroup em terminated ou ausentes
LoadBalancers: []
ECR repositories: []
```

[Voltar ao README principal](../README.md)
