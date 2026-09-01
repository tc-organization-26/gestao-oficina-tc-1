# Infraestrutura com Terraform e Kubernetes

[Voltar ao README principal](../README.md)

Este diretório provisiona os recursos Kubernetes da Oficina API usando Terraform.

O Terraform cria recursos individuais pelo provider Kubernetes.

Para instruções de execução, consulte [`../docs/EXECUCAO.md`](../docs/EXECUCAO.md). Para problemas comuns, consulte [`../docs/TROUBLESHOOTING.md`](../docs/TROUBLESHOOTING.md).

## Recursos criados

- Namespace `oficina`.
- ConfigMap e Secret do PostgreSQL.
- PVC `postgres-data` para persistência dos dados do PostgreSQL.
- Deployment e Service interno do PostgreSQL.
- ConfigMap e Secret da API.
- Secret opcional `ghcr-credentials` para pull de imagem privada no GHCR.
- Deployment e Service da API. Localmente pode usar `NodePort`; em EKS o CD usa `LoadBalancer`.
- HPA da API por CPU e memória.

Em cloud AWS com Amazon EKS, o cluster Kubernetes é criado fora deste diretório, normalmente com `eksctl`. Depois que o `kubectl` aponta para o EKS, este Terraform pode criar os recursos da aplicação dentro do cluster.

## Arquivos Terraform

- `providers.tf`: configura a versão do Terraform, o provider Kubernetes e o backend Kubernetes.
- `variables.tf`: declara variáveis da infraestrutura, incluindo valores sensíveis.
- `main.tf`: cria os recursos Kubernetes.
- `outputs.tf`: expõe informações úteis após o `terraform apply`.
- `terraform.tfvars.example`: exemplo para criar o arquivo local `terraform.tfvars`.

O arquivo `terraform.tfvars` não deve ser versionado, pois pode conter senha do banco e segredo JWT.

## Desenho da infraestrutura

```text
Namespace oficina
  |
  |-- Deployment oficina-api
  |     |-- ConfigMap oficina-api-config
  |     |-- Secret oficina-api-secret
  |     |-- Secret ghcr-credentials, quando configurado
  |     |-- Service oficina-api, NodePort 30081 localmente ou LoadBalancer em EKS
  |     |-- HPA oficina-api
  |
  |-- Deployment postgres
        |-- ConfigMap postgres-config
        |-- Secret postgres-secret
        |-- PVC postgres-data
        |-- Service postgres, ClusterIP
```

## Execução em Amazon EKS

O deploy em AWS usa dois níveis de infraestrutura:

```text
Infraestrutura AWS
  |
  |-- Amazon EKS
  |-- EC2 Managed Node Group
  |-- VPC, subnets, security groups e internet gateway
  |-- Amazon ECR com a imagem oficina-api
  |-- Elastic Load Balancer, quando o Service da API é LoadBalancer
  |
  +-- Recursos Kubernetes do projeto
        |-- Namespace oficina
        |-- PostgreSQL
        |-- Oficina API
        |-- ConfigMaps e Secrets
        |-- Services
```

O `eksctl` cria a parte AWS: cluster EKS, nodegroup EC2, rede e stacks CloudFormation. O Terraform deste diretório cria a parte Kubernetes da aplicação.

No AWS Academy, pode ser necessário criar o cluster com uma role existente do laboratório, como `LabRole`, porque o usuário temporário pode não ter permissão para criar novas IAM Roles.

Em conta pessoal AWS, o `eksctl` pode criar as roles automaticamente se o usuário tiver permissões suficientes. Nesse cenário há cobrança real por EKS, EC2, Load Balancer, NAT Gateway, EBS e armazenamento de imagem no ECR enquanto os recursos existirem.

## Imagem da aplicação em cloud

Localmente, o valor padrão é:

```hcl
app_image = "oficina-api:local"
```

Em EKS, a imagem precisa estar em um registry acessível pelo cluster, como Amazon ECR:

```hcl
app_image = "ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest"
```

Fluxo esperado:

```text
docker build
  -> docker tag para ECR
  -> docker push para ECR
  -> Kubernetes Deployment usa a imagem publicada
```

## Service local e Service em cloud

O Terraform permite escolher o tipo do Service com `app_service_type`. O valor padrão é `NodePort` para facilitar execução em Kubernetes local:

```text
http://localhost:30081/swagger-ui/index.html
```

Em Amazon EKS, para acesso público, use Service do tipo `LoadBalancer`. Isso cria automaticamente um Elastic Load Balancer na AWS:

```hcl
app_service_type = "LoadBalancer"
```

Em laboratório, também é possível validar sem exposição pública usando:

```bash
kubectl port-forward service/oficina-api 18081:8081 -n oficina
```

```text
http://localhost:18081/swagger-ui/index.html
```

## Backend do Terraform

O projeto usa backend Kubernetes para armazenar o state em um Secret no namespace `default` do cluster.

Na execução local:

```powershell
terraform init -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

No Continuous Deployment, o workflow usa o kubeconfig recebido pelo secret `KUBE_CONFIG_BASE64` e inicializa o backend com `secret_suffix=oficina-api`.

## Relação com os manifestos YAML

Os recursos principais da API e do banco são gerenciados pelo Terraform.

Os manifestos em `k8s/cd` são complementares e aplicados pelo GitHub Actions após o `terraform apply`. Isso evita duplicar via `kubectl apply` os recursos que já são controlados pelo Terraform.

## Observações

- O banco roda dentro do Kubernetes usando PostgreSQL 16 Alpine.
- Os dados do banco ficam no PVC `postgres-data`.
- ConfigMaps guardam configurações não sensíveis.
- Secrets guardam usuários, senhas, tokens e credenciais de registry.
- O HPA depende do Metrics Server instalado no cluster.
- Em EKS com PVC, normalmente é necessário o add-on `aws-ebs-csi-driver`.
- Em demonstrações rápidas no AWS Academy, é possível usar `emptyDir` no PostgreSQL para evitar dependência de EBS, mas os dados não persistem se o pod for recriado.
- Antes de sair do laboratório ou encerrar testes em conta pessoal, remova EKS, EC2 nodes, Load Balancer e ECR para evitar recursos ligados.

[Voltar ao README principal](../README.md)
