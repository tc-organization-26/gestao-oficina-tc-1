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

## Continuous Deployment com GitHub Actions

O workflow `.github/workflows/cicd.yml` executa build, testes, build/push da imagem Docker e deploy automatico pelo runner do GitHub.

Quando ha push nas branches `main` ou `master`, o deploy acontece automaticamente apos a pipeline passar. Nao ha etapa de aprovacao manual configurada no workflow, caracterizando Continuous Deployment.

Para nao depender da maquina local, o Terraform usa backend Kubernetes e grava o state em um Secret no namespace `default` do cluster. O workflow inicializa esse backend com o kubeconfig recebido por secret.

Configure estes secrets no repositorio:

- `KUBE_CONFIG_BASE64`: kubeconfig do cluster codificado em Base64.
- `POSTGRES_USER`: usuario do PostgreSQL.
- `POSTGRES_PASSWORD`: senha do PostgreSQL.
- `JWT_SECRET`: segredo JWT da aplicacao.
- `GHCR_USERNAME`: usuario do GitHub Container Registry, necessario se a imagem estiver privada.
- `GHCR_TOKEN`: token com permissao de leitura no GitHub Container Registry, necessario se a imagem estiver privada.

Configure estas variables no repositorio quando precisar sobrescrever os valores padrao:

- `KUBE_CONTEXT`: contexto Kubernetes do kubeconfig.
- `POSTGRES_STORAGE_CLASS_NAME`: StorageClass usado pelo PVC do PostgreSQL. O padrao do workflow e `hostpath`.

O deploy do banco de dados e da aplicacao e feito pelo Terraform. Os manifestos YAML aplicados pelo workflow ficam em `k8s/cd` e devem ser usados apenas para recursos complementares, evitando duplicar no `kubectl apply` os recursos que ja sao gerenciados pelo Terraform.

## Execucao local

O passo a passo de execucao local fica centralizado no `README.md` principal, na secao `Como executar localmente`.

La estao os comandos de Docker Compose, Kubernetes com Terraform, criacao de `terraform.tfvars`, confirmacao com `yes` no `terraform apply`, uso de `terraform init -reconfigure` quando o backend precisar ser reinicializado, verificacoes com `kubectl` e acesso ao Swagger.

Este arquivo é referencia tecnica dos recursos Terraform presentes no diretorio `infra/`.

## Observacoes

- O banco roda dentro do Kubernetes usando PostgreSQL.
- Os dados do banco ficam no PVC `postgres-data`.
- ConfigMaps guardam configuracoes nao sensiveis.
- Secrets guardam usuarios, senhas e tokens.
- O HPA depende do metrics-server no cluster local. Se o cluster nao tiver metrics-server, o HPA sera criado, mas pode nao coletar metricas ate esse componente ser instalado.
