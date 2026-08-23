# INFRA-README - Infraestrutura Local com Terraform e Kubernetes

Este diretorio provisiona os recursos Kubernetes locais da Oficina API usando Terraform.

O Terraform cria recursos individuais do provider Kubernetes. Ele nao usa `local-exec` nem chama `kubectl apply`.

Para o passo a passo completo de execucao local, consulte tambem o `README.md` principal do projeto.

## Recursos Criados

- Namespace `oficina`.
- ConfigMap e Secret do PostgreSQL.
- PVC para persistencia dos dados do PostgreSQL.
- Deployment e Service interno do PostgreSQL.
- ConfigMap e Secret da API.
- Deployment e Service `NodePort` da API.
- HPA da API por CPU e memoria.

## Arquivos Terraform

- `providers.tf`: configura a versao do Terraform, o provider Kubernetes e o kubeconfig local.
- `variables.tf`: declara as variaveis usadas pela infraestrutura, incluindo dados sensiveis.
- `main.tf`: cria os recursos Kubernetes individualmente.
- `outputs.tf`: mostra informacoes uteis apos o `terraform apply`, como namespace, service e URL local.
- `terraform.tfvars.example`: exemplo para criar o arquivo local `terraform.tfvars`.

O arquivo `terraform.tfvars` nao deve ser versionado, pois contem senha do banco e segredo JWT.

## Pre-requisitos

- Docker Desktop com Kubernetes habilitado, Minikube ou Kind.
- Docker.
- Terraform.
- `kubectl` apontando para o cluster local.

Para Docker Desktop, o contexto padrao costuma ser:

```powershell
kubectl config use-context docker-desktop
```

## Como Aplicar Localmente

Sequencia rapida:

```powershell
cd "C:\Users\thais\OneDrive\Area de Trabalho\proj-tc-1-fiap"
kubectl config use-context docker-desktop
kubectl cluster-info
docker build -t oficina-api:local .
cd infra
copy terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
kubectl get all -n oficina
kubectl get pvc -n oficina
kubectl get hpa -n oficina
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

O caminho `C:\Users\thais\OneDrive\Area de Trabalho\proj-tc-1-fiap` representa a raiz do projeto nesta maquina. Em outra maquina, entre na pasta raiz onde o repositorio foi clonado antes de executar os comandos.

Com o `port-forward` aberto, acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

As portas locais ficam diferentes por decisao do projeto:

```text
Docker Compose: http://localhost:8081
Kubernetes com port-forward: http://localhost:18081
```

Ao usar a collection do Insomnia, ajuste a base URL conforme o modo de execucao.

## Variaveis do Terraform

Depois configure as variaveis sensiveis do Terraform:

```powershell
copy infra\terraform.tfvars.example infra\terraform.tfvars
```

Edite `infra\terraform.tfvars` e troque as senhas e o segredo JWT.

Exemplo valido:

```hcl
kubeconfig_context = "docker-desktop"
postgres_storage_class_name = "hostpath"

postgres_user     = "postgres"
postgres_password = "troque_aqui"
jwt_secret        = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

Nao use a sintaxe `${SPRING_DATASOURCE_USERNAME:postgres}` no `terraform.tfvars`; essa sintaxe e do Spring, nao do Terraform.

Tambem e possivel usar variaveis de ambiente do Terraform:

```powershell
$env:TF_VAR_kubeconfig_context = "docker-desktop"
$env:TF_VAR_postgres_storage_class_name = "hostpath"
$env:TF_VAR_postgres_user = "postgres"
$env:TF_VAR_postgres_password = "troque_aqui"
$env:TF_VAR_jwt_secret = "troque_por_uma_chave_segura_com_32_bytes_ou_mais"
```

## PVC e StorageClass

Se o PVC do PostgreSQL travar em `Still creating` e depois falhar com `context deadline exceeded`, confira o StorageClass local:

```powershell
kubectl get storageclass
```

Use `hostpath` para Docker Desktop ou `standard` para Minikube em `postgres_storage_class_name`.

Se o StorageClass estiver com `VOLUMEBINDINGMODE` igual a `WaitForFirstConsumer`, o PVC so sera vinculado depois que o pod do PostgreSQL existir. Por isso o recurso Terraform do PVC usa `wait_until_bound = false`.

## Comandos de Verificacao

```powershell
kubectl get all -n oficina
kubectl get hpa -n oficina
kubectl get pvc -n oficina
terraform plan
```

## Acesso a API

No Docker Desktop, o `NodePort` pode responder em:

```text
http://localhost:30081/swagger-ui/index.html
```

Se o `NodePort` nao responder no Kubernetes local, use:

```powershell
kubectl port-forward -n oficina service/oficina-api 18081:8081
```

E acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

Nesse comando, `18081` e a porta local. Se ela ja estiver ocupada, troque por outra porta, por exemplo:

```powershell
kubectl port-forward -n oficina service/oficina-api 18082:8081
```

Nesse caso, acesse `http://localhost:18082/swagger-ui/index.html` e ajuste a base URL da collection do Insomnia para `http://localhost:18082`.

## Observacoes

- O banco roda dentro do Kubernetes usando PostgreSQL.
- Os dados do banco ficam no PVC `postgres-data`.
- ConfigMaps guardam configuracoes nao sensiveis.
- Secrets guardam usuarios, senhas e tokens.
- O HPA depende do metrics-server no cluster local. Se o cluster nao tiver metrics-server, o HPA sera criado, mas pode nao coletar metricas ate esse componente ser instalado.
