# Troubleshooting

[Voltar ao README principal](../README.md)

Este documento reúne problemas comuns de execução local, Docker, Java, Maven, PostgreSQL, Kubernetes, Terraform, GitHub Actions e API.

A ordem começa pelos problemas mais simples e avança para os mais complexos. Em uma primeira execução local, verifique primeiro `Docker e Docker Compose`, depois `Variáveis de ambiente` e então `PostgreSQL e Flyway`.

## Menu

- [Docker e Docker Compose](#docker-e-docker-compose)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [PostgreSQL e Flyway](#postgresql-e-flyway)
- [Java e Maven](#java-e-maven)
- [API, Swagger e autenticação](#api-swagger-e-autenticação)
- [Kubernetes](#kubernetes)
- [AWS Academy, EKS e ECR](#aws-academy-eks-e-ecr)
- [Terraform](#terraform)
- [GitHub Actions e runner](#github-actions-e-runner)

## Docker e Docker Compose

### Docker Desktop não está rodando

Erro comum:

```text
failed to connect to the docker API
```

Abra o Docker Desktop, aguarde a inicialização completa e teste:

```powershell
docker version
```

O comando precisa mostrar informações de `Client` e `Server`.

### Docker Compose não reconhecido

Erro comum:

```text
docker: 'compose' is not a docker command
```

Verifique se o Docker Desktop está atualizado. Em instalações antigas, tente:

```bash
docker-compose up --build
```

Se `docker-compose` funcionar, ele pode ser usado no lugar de `docker compose`.

### Containers não sobem após alteração recente

Quando houver dúvida se a imagem local ficou desatualizada, reconstrua:

```bash
docker compose up --build
```

Se quiser forçar build sem cache:

```bash
docker compose build --no-cache
docker compose up
```

### Porta da API já está em uso

Se a porta `8081` estiver ocupada, altere o mapeamento da API em `docker-compose.yml`:

```yaml
ports:
  - "8082:8081"
```

Depois acesse:

```text
http://localhost:8082/swagger-ui/index.html
```

### Porta do PostgreSQL já está em uso

No `docker-compose.yml`, o PostgreSQL local está publicado como `5433:5432`.

Se a porta local também estiver ocupada, altere o lado esquerdo do mapeamento:

```yaml
ports:
  - "5434:5432"
```

Essa mudança afeta apenas o acesso a partir da máquina host. A aplicação em Docker Compose continua acessando o banco internamente por `postgres:5432`.

### Aplicação inicia antes do banco

Se aparecer erro de conexão recusada com o PostgreSQL, aguarde alguns segundos e rode novamente:

```bash
docker compose up --build
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

## Variáveis de ambiente

### Arquivo `.env` não encontrado

Crie o arquivo a partir do exemplo:

```powershell
copy .env.example .env
```

Em Bash:

```bash
cp .env.example .env
```

Depois preencha usuário, senha e segredo JWT.

### Variável obrigatória aparece como vazia no Docker Compose

Se o Docker Compose mostrar mensagem parecida com:

```text
The "POSTGRES_PASSWORD" variable is not set. Defaulting to a blank string.
```

Confira se o arquivo `.env` está na raiz do projeto, no mesmo nível de `docker-compose.yml`, e se as chaves estão sem aspas extras:

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

Depois suba novamente:

```bash
docker compose up --build
```

### Arquivo criado como `.env.txt` no Windows

Se você criou o arquivo pelo Bloco de Notas, o Windows pode salvar como `.env.txt`.

Confira na raiz do projeto:

```powershell
Get-ChildItem -Force
```

O nome precisa aparecer exatamente como:

```text
.env
```

### Senha do banco diferente da senha da aplicação

Se aparecer autenticação recusada no PostgreSQL, confira se os valores abaixo são iguais:

```env
POSTGRES_PASSWORD=troque_aqui
SPRING_DATASOURCE_PASSWORD=troque_aqui
```

Depois reinicie:

```bash
docker compose down
docker compose up --build
```

### `JWT_SECRET` vazio, curto ou com espaço extra

Use uma chave com pelo menos 32 caracteres:

```env
JWT_SECRET=minha_chave_super_secreta_com_mais_de_32_caracteres
```

Evite espaço no fim da linha:

```env
JWT_SECRET=oficina-jwt-secret-key-32-bytes
```

Não use:

```env
JWT_SECRET=oficina-jwt-secret-key-32-bytes<espaco>
```

No Kubernetes com Terraform, o valor equivalente fica em `infra/terraform.tfvars`:

```hcl
jwt_secret = "minha_chave_super_secreta_com_mais_de_32_caracteres"
```

## PostgreSQL e Flyway

### Banco inicializado com credenciais antigas

Se você alterou `POSTGRES_DB`, `POSTGRES_USER` ou `POSTGRES_PASSWORD` depois da primeira execução, o volume pode manter as credenciais antigas.

Para recriar o banco local:

```bash
docker compose down -v
docker compose up --build
```

Esse comando apaga os dados locais do PostgreSQL.

### Erro nas migrations Flyway

Se aparecer erro relacionado ao Flyway, o banco local pode estar com uma versão antiga do schema.

Em ambiente local de desenvolvimento, recrie o volume:

```bash
docker compose down -v
docker compose up --build
```

Esse comando apaga os dados locais.

### Aplicação fora do Docker não conecta no banco

No Docker Compose, a API usa o host `postgres`, porque está na mesma rede dos containers.

Fora do Docker, use `localhost` ou `127.0.0.1` e a porta publicada no host. Neste projeto, para testes locais, a porta do banco costuma ser `5433`:

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5433/oficina_db_2
```

### PostgreSQL não está saudável

Confira o estado dos containers:

```bash
docker compose ps
```

Veja os logs do banco:

```bash
docker compose logs -f postgres
```

Se o banco estiver reiniciando por credenciais ou volume antigo, recrie o volume local com `docker compose down -v`.

## Java e Maven

### Java não encontrado

Erro comum:

```text
The java/javac command does not exist in PATH nor is JAVA_HOME set
```

Instale o JDK 21 e confira:

```bash
java -version
javac -version
```

No Windows, confira também se `JAVA_HOME` aponta para o JDK 21.

### Java em versão incorreta

O projeto exige Java 21. Se aparecer erro como `release version 21 not supported`, verifique:

```bash
java -version
```

Selecione uma instalação do JDK 21 antes de executar:

```bash
./mvnw clean package
```

No Windows:

```powershell
.\mvnw.cmd clean package
```

### Maven Wrapper sem permissão no Linux/macOS

Erro comum:

```text
permission denied: ./mvnw
```

Corrija a permissão:

```bash
chmod +x mvnw
./mvnw clean package
```

### Maven Wrapper não inicia no Windows

Se aparecer uma mensagem parecida com:

```text
Cannot start maven from wrapper
```

Confira primeiro se o Java 21 está no `PATH`:

```powershell
java -version
```

Depois tente executar o wrapper a partir da raiz do projeto:

```powershell
.\mvnw.cmd -v
.\mvnw.cmd clean package
```

Se continuar falhando, use o Docker Compose para validar a aplicação sem depender do Maven local:

```bash
docker compose up --build
```

### Build Maven não baixa dependências

Verifique conexão com a internet, VPN ou proxy.

Com Docker:

```bash
docker compose build --no-cache
docker compose up
```

Com Maven local:

```bash
./mvnw -U clean package
```

### Testes falham por conexão recusada no PostgreSQL

Os testes automatizados usam PostgreSQL em `127.0.0.1:5433`, conforme `src/test/resources/application.properties` e a configuração do Maven Surefire.

Se aparecer erro como `Connection refused`, suba apenas o banco pelo Docker Compose antes dos testes:

```bash
docker compose up -d postgres
```

Depois execute:

```powershell
.\mvnw.cmd test
```

Em Linux/macOS:

```bash
./mvnw test
```

Se a porta local `5433` estiver ocupada e você mudar o mapeamento do PostgreSQL no `docker-compose.yml`, ajuste também a URL de teste ou execute os testes com a propriedade correspondente.

## API, Swagger e autenticação

### Swagger não abre

Confira se a aplicação está rodando:

```bash
docker compose ps
docker compose logs -f app
```

URL padrão no Docker Compose:

```text
http://localhost:8081/swagger-ui/index.html
```

Se você mudou a porta no `docker-compose.yml`, use a nova porta.

### Endpoint retorna 401 Unauthorized

Alguns endpoints exigem autenticação.

Faça login:

```text
POST /auth/login
```

Depois envie o token:

```text
Authorization: Bearer seu_token_aqui
```

Se o Swagger UI não mostrar o botão `Authorize`, use a collection do Insomnia ou outro cliente HTTP e envie o header `Authorization`. O filtro de segurança do projeto espera o prefixo `Bearer ` no header.

### Collection do Insomnia aponta para porta errada

Ajuste a base URL conforme o modo de execução:

```text
Docker Compose: http://localhost:8081
Kubernetes com port-forward: http://localhost:18081
```

Para executar o fluxo principal, use `Run folder` na pasta `FLUXO COMPLETO`.

### Dados de entrada geram erro de regra de negócio

Alguns erros são esperados quando a transição viola uma regra do domínio. Exemplos:

- Tentar entregar OS que ainda não foi paga.
- Tentar finalizar OS que não está em execução.
- Tentar aprovar orçamento que ainda não foi enviado.
- Tentar baixar estoque com quantidade indisponível.

Nesses casos, confira o status atual da OS ou do orçamento antes de chamar a próxima rota.

## Kubernetes

### Kubernetes não está habilitado no Docker Desktop

No Docker Desktop:

1. Abra `Settings`.
2. Acesse `Kubernetes`.
3. Marque `Enable Kubernetes`.
4. Clique em `Apply & Restart`.
5. Aguarde o status ficar `Running`.

Depois rode:

```powershell
kubectl cluster-info
```

### Kubernetes sem contexto configurado

Erro comum:

```text
error: current-context is not set
```

No Docker Desktop:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

Se `docker-desktop` não aparecer, o Kubernetes do Docker Desktop ainda não foi habilitado ou não terminou de iniciar.

### Namespace `oficina` não existe

Se comandos com `-n oficina` falharem, confirme se o Terraform já criou o namespace:

```powershell
kubectl get namespace
```

Se não existir, execute o provisionamento:

```powershell
cd infra
terraform apply
```

### Imagem local não encontrada no Kubernetes

Se o pod da API ficar em `ImagePullBackOff` ou `ErrImageNeverPull`, gere a imagem antes do `terraform apply`:

```powershell
docker build -t oficina-api:local .
cd infra
terraform apply
```

No Docker Desktop, o cluster local consegue enxergar imagens criadas pelo Docker local. Em Minikube ou Kind, pode ser necessário carregar a imagem no cluster.

### Pod da API não fica pronto

Confira os pods:

```powershell
kubectl get pods -n oficina
```

Veja detalhes do pod:

```powershell
kubectl describe pod -n oficina -l app=oficina-api
```

Veja logs da aplicação:

```powershell
kubectl logs -n oficina -l app=oficina-api
```

Problemas comuns nessa etapa são senha incorreta do banco, Secret JWT ausente, banco ainda não pronto ou imagem incorreta.

### NodePort não abre no localhost

Se `http://localhost:30081` não abrir, use `port-forward`, que costuma ser mais previsível em ambiente local:

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

### Porta do `port-forward` está ocupada

Troque a porta local:

```powershell
kubectl port-forward -n oficina service/oficina-api 18082:8081
```

Depois use:

```text
http://localhost:18082/swagger-ui/index.html
```

### HPA sem métricas

Se o HPA aparecer com métricas indisponíveis, o Metrics Server pode não estar instalado:

```powershell
kubectl get hpa -n oficina
kubectl top pods -n oficina
```

Instale o Metrics Server:

```powershell
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl rollout status deployment/metrics-server -n kube-system
```

No Docker Desktop, se houver erro de certificado do kubelet:

```powershell
kubectl patch deployment metrics-server -n kube-system --type=strategic --patch-file k8s/metrics-server-docker-desktop-patch.json
kubectl rollout status deployment/metrics-server -n kube-system
```

## AWS Academy, EKS e ECR

### `eksctl create cluster` falha com `iam:CreateRole`

Erro observado no AWS Academy:

```text
User is not authorized to perform: iam:CreateRole
Encountered a permissions error performing a tagging operation
```

O `eksctl create cluster` padrão tenta criar IAM Roles automaticamente para o control plane e para os nodes. Em alguns laboratórios AWS Academy, o usuário temporário não tem permissão para `iam:CreateRole`.

Verifique se existe uma role pronta do laboratório:

```bash
aws iam list-roles --query "Roles[?contains(RoleName, 'LabRole')].[RoleName,Arn]" --output table
```

Se existir `LabRole`, crie o cluster com um arquivo `cluster.yaml` usando a role existente:

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

Depois:

```bash
eksctl create cluster -f cluster.yaml
```

Se ainda falhar com `iam:PassRole` ou `eks:CreateCluster`, o laboratório não tem permissão suficiente para EKS. Nesse caso, use outro laboratório com EKS habilitado ou peça ajuste de permissão ao instrutor.

### Stack `eksctl-oficina-cluster-cluster` já existe

Erro comum após uma tentativa falha:

```text
AlreadyExistsException: Stack [eksctl-oficina-cluster-cluster] already exists
```

Isso significa que o cluster não existe, mas a stack do CloudFormation ficou em `ROLLBACK_COMPLETE`.

Liste as stacks:

```bash
aws cloudformation list-stacks \
  --region us-east-1 \
  --stack-status-filter CREATE_FAILED ROLLBACK_COMPLETE DELETE_FAILED \
  --query "StackSummaries[?contains(StackName, 'oficina')].[StackName,StackStatus]" \
  --output table
```

Apague a stack:

```bash
aws cloudformation delete-stack \
  --region us-east-1 \
  --stack-name eksctl-oficina-cluster-cluster
```

Depois tente novamente:

```bash
eksctl create cluster -f cluster.yaml
```

### Stack não apaga por `TerminationProtection`

Erro observado:

```text
Stack cannot be deleted while TerminationProtection is enabled
```

Desative a proteção:

```bash
aws cloudformation update-termination-protection \
  --region us-east-1 \
  --stack-name eksctl-oficina-cluster-cluster \
  --no-enable-termination-protection
```

Depois apague:

```bash
aws cloudformation delete-stack \
  --region us-east-1 \
  --stack-name eksctl-oficina-cluster-cluster
```

### `kubectl get nodes` falha com `ExecCredential v1alpha1`

Erro observado:

```text
invalid apiVersion "client.authentication.k8s.io/v1alpha1"
no kind "ExecCredential" is registered for version "client.authentication.k8s.io/v1alpha1"
```

Esse erro normalmente indica incompatibilidade entre `kubectl`, AWS CLI e o kubeconfig gerado.

Instale uma versão nova do `kubectl`:

```bash
mkdir -p $HOME/bin
export PATH=$HOME/bin:$PATH

curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.34.9/2026-07-05/bin/linux/amd64/kubectl
chmod +x ./kubectl
mv ./kubectl $HOME/bin/kubectl

kubectl version --client
```

Instale AWS CLI v2 no usuário do lab:

```bash
cd ~
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -oq awscliv2.zip
./aws/install -i $HOME/aws-cli -b $HOME/bin --update
export PATH=$HOME/bin:$PATH

aws --version
```

Recrie o kubeconfig:

```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name oficina-cluster
```

Teste:

```bash
kubectl get nodes
```

Se ainda aparecer `v1alpha1`, corrija temporariamente o kubeconfig:

```bash
sed -i 's/client.authentication.k8s.io\/v1alpha1/client.authentication.k8s.io\/v1beta1/g' ~/.kube/config
kubectl get nodes
```

### Docker no terminal do AWS Academy pede senha de `sudo`

Erro observado:

```text
permission denied while trying to connect to the Docker daemon socket
```

O terminal do AWS Academy pode não permitir acesso ao Docker daemon pelo usuário do lab. Se `sudo docker ps` pedir senha, faça o build e push da imagem no computador local usando as credenciais temporárias do laboratório.

No PowerShell local:

```powershell
aws configure set aws_access_key_id "ACCESS_KEY_DO_LAB"
aws configure set aws_secret_access_key "SECRET_KEY_DO_LAB"
aws configure set aws_session_token "SESSION_TOKEN_DO_LAB"
aws configure set region "us-east-1"

aws sts get-caller-identity
```

Depois publique a imagem no ECR a partir da raiz do projeto:

```powershell
cd [pasta raiz do seu projeto]

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

docker build -t oficina-api .
docker tag oficina-api:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
```

### `docker build` falha com `Dockerfile: no such file or directory`

Erro observado:

```text
failed to read dockerfile: open Dockerfile: no such file or directory
```

O comando foi executado fora da raiz do projeto. Entre na pasta correta:

```powershell
cd [pasta raiz do seu projeto]
dir Dockerfile
docker build -t oficina-api .
```

### Push para o ECR falha porque a imagem não existe

Erros comuns:

```text
No such image: oficina-api:latest
tag does not exist
```

Isso acontece quando o `docker build` falhou antes. Faça novamente a sequência completa:

```powershell
docker build -t oficina-api .
docker tag oficina-api:latest ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
docker push ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/oficina-api:latest
```

### Pod da API fica em `ImagePullBackOff` no EKS

Investigue:

```bash
kubectl describe pod -n oficina -l app=oficina-api
```

Causas comuns:

- Imagem não foi enviada para o ECR.
- `ACCOUNT_ID` ou região no nome da imagem estão incorretos.
- Repositório ECR foi apagado.
- Node não tem permissão para puxar a imagem.

Confira o repositório:

```bash
aws ecr describe-repositories --region us-east-1
```

Confira a imagem configurada no deployment:

```bash
kubectl get deployment oficina-api -n oficina -o jsonpath="{.spec.template.spec.containers[0].image}"
```

### Service `LoadBalancer` não mostra endereço externo

Confira:

```bash
kubectl get service oficina-api -n oficina
kubectl describe service oficina-api -n oficina
```

Em EKS, o Service do tipo `LoadBalancer` cria um Elastic Load Balancer. Pode levar alguns minutos para aparecer o endereço externo.

Se o endereço não aparecer e houver eventos de erro, verifique permissões do laboratório ou use `port-forward` para validação:

```bash
kubectl port-forward service/oficina-api 18081:8081 -n oficina
```

Depois acesse:

```text
http://localhost:18081/swagger-ui/index.html
```

### Conferir se nada ficou cobrando na AWS

Após remover o ambiente, confira:

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
EKS: []
EC2: instâncias do nodegroup terminated ou ausentes
LoadBalancers: []
ECR repositories: []
```

Instâncias EC2 com estado `terminated` não ficam cobrando computação, mas podem aparecer no histórico por algum tempo.

## Terraform

### Terraform não reconhecido

Erro comum:

```text
terraform : O termo 'terraform' não é reconhecido
```

Instale o Terraform ou adicione a pasta do `terraform.exe` ao `PATH`. Depois abra um novo terminal:

```powershell
terraform version
```

### Terraform executado na pasta errada

Execute `terraform init`, `terraform plan`, `terraform apply` e `terraform destroy` dentro de `infra`:

```powershell
cd infra
dir
```

A pasta deve conter:

```text
main.tf
providers.tf
variables.tf
outputs.tf
terraform.tfvars
```

### Arquivo `terraform.tfvars` não existe

Crie a partir do exemplo:

```powershell
copy infra\terraform.tfvars.example infra\terraform.tfvars
```

Depois edite:

```powershell
notepad infra\terraform.tfvars
```

### `Invalid character` no Terraform

Erro comum:

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

### Terraform pede `secret_suffix`

Se o Terraform pedir `secret_suffix` ou informar backend não inicializado, reconfigure o backend:

```powershell
kubectl config use-context docker-desktop
kubectl cluster-info
cd infra
terraform init -reconfigure -backend-config="config_path=$env:USERPROFILE\.kube\config" -backend-config="namespace=default" -backend-config="secret_suffix=oficina-api-local"
```

### Terraform não baixa provider

Se `terraform init` falhar com download do provider:

```powershell
terraform init -upgrade
```

Verifique internet, VPN ou proxy.

### PVC do PostgreSQL travado

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

### `terraform apply` fica preso ou falha no cluster

Confirme que o `kubectl` aponta para o cluster certo:

```powershell
kubectl config current-context
kubectl cluster-info
```

Depois confira os recursos parcialmente criados:

```powershell
kubectl get all,pvc,hpa -n oficina
```

Se o problema for configuração de backend, rode `terraform init -reconfigure` conforme a seção anterior.

## GitHub Actions e runner

### Deploy fica aguardando runner

Mensagem comum no GitHub Actions:

```text
Waiting for a runner to pick up this job...
Requested labels: self-hosted, Windows
```

O workflow precisa de um runner self-hosted Windows online. Confira:

- O runner está ligado.
- O runner aparece como `Online` em `Settings > Actions > Runners`.
- O runner tem os labels `self-hosted` e `Windows`.
- O runner foi registrado no repositório ou organização correta.
- A máquina do runner tem acesso ao cluster Kubernetes.

### Runner não tem ferramentas de deploy

Na máquina do runner, confira:

```powershell
kubectl version --client
terraform version
docker version
```

Se o Terraform não estiver no `PATH`, configure a variable `TERRAFORM_EXE` no GitHub Actions:

```text
C:\Program Files\Terraform\terraform.exe
```

### `KUBE_CONFIG_BASE64` inválido

Se o deploy não conseguir acessar o cluster, gere novamente o secret no PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\.kube\config"))
```

Cole o valor em `Settings > Secrets and variables > Actions > Secrets`.

### Imagem privada no GHCR não é baixada pelo cluster

Se o pod falhar ao puxar imagem privada, configure:

- `GHCR_USERNAME`
- `GHCR_TOKEN`

O Terraform cria o Secret `ghcr-credentials` quando esses valores são informados.

### Serviço do runner no Windows não inicia

Se o serviço do runner falhar ao iniciar, verifique a conta configurada no serviço.

Em alguns casos, usar `AUTORIDADE NT\SERVIÇO DE REDE` pode impedir acesso à pasta do runner, Docker Desktop, kubeconfig ou Terraform. Ajuste em `services.msc` para usar o usuário Windows que tem acesso às ferramentas.

Também é possível manter o runner rodando manualmente:

```powershell
.\run.cmd
```

[Voltar ao README principal](../README.md)
