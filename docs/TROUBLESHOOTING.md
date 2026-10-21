# Troubleshooting

[Voltar ao README principal](../README.md)

## Docker Compose local

Se Docker não estiver disponível, confirme que o Docker Desktop está iniciado:

```powershell
docker version
docker compose version
```

Se a porta `8081` ou `5433` já estiver ocupada, encerre o processo conflitante ou altere o mapeamento correspondente em `docker-compose.yml`.

Se o banco local contiver credenciais antigas:

```powershell
docker compose down --volumes
docker compose up --build
```

Esse comando apaga somente o volume local do Compose; não afeta o banco gerenciado da AWS.

## Build e testes

O projeto exige Java 21. Confira:

```powershell
java -version
./mvnw.cmd -version
./mvnw.cmd clean verify
```

Se o Maven não conseguir baixar dependências, verifique proxy, acesso à internet e certificado da rede antes de repetir o build.

## Bootstrap

### Credenciais do Lab expiraram

Atualize os três secrets `BOOTSTRAP_AWS_*` no repositório `oficina-k8s-infra` e execute novamente o workflow manual. Se o bucket de state ainda existir, o script usa o fluxo normal e não recria o bootstrap.

### Conta AWS diferente

O script compara o `aws_account_id` gravado no state com `aws sts get-caller-identity`. Se forem diferentes, ele interrompe a execução para evitar aplicar o state em outra conta.

### GitHub retorna 404 ao consultar um repositório

Confirme que os quatro repositórios remotos já existem e que `GH_ADMIN_TOKEN` consegue lê-los. Repositórios privados exigem um token com acesso à organização.

### Não foi possível criar OIDC ou IAM role

Alguns laboratórios educacionais restringem operações IAM. Verifique no histórico do workflow qual ação foi negada. A automação não consegue contornar uma política da conta; nesse caso, a instituição precisa liberar as ações ou fornecer uma role compatível.

## Terraform

### State lock

Os repositórios usam locking nativo do backend S3 por `.tflock`. Aguarde outro workflow terminar antes de executar novamente. Só remova um lock após confirmar que nenhuma execução está ativa.

### Bucket não encontrado

Execute primeiro o workflow `Bootstrap AWS e GitHub` do repositório `oficina-k8s-infra`. Ele cria o bucket e configura `TF_STATE_BUCKET` nos quatro repositórios.

### Provider OIDC já existe

O script detecta o provider `token.actions.githubusercontent.com` pelo ARN e passa seu valor ao Terraform, evitando tentar criá-lo novamente.

## Deploy no EKS

### A imagem privada não é baixada

Confira os secrets `GHCR_USERNAME` e `GHCR_TOKEN` no repositório da aplicação. O token precisa de `read:packages` e acesso ao pacote.

```powershell
kubectl describe pod -n oficina -l app=oficina-api
kubectl get secret ghcr-credentials -n oficina
```

### A aplicação não conecta ao banco

Confira se o repositório `oficina-db-infra` terminou o apply e publicou os parâmetros esperados:

```powershell
aws ssm get-parameters-by-path --path /oficina/hom/database --recursive
kubectl get configmap oficina-api-config -n oficina -o yaml
kubectl logs deployment/oficina-api -n oficina
```

Para produção, troque `hom` por `prod`.

### HPA não apresenta métricas

```powershell
kubectl get deployment metrics-server -n kube-system
kubectl top pods -n oficina
kubectl describe hpa oficina-api -n oficina
```

O Metrics Server é gerenciado pelo Terraform do repositório `oficina-k8s-infra`.

### Rollout não conclui

```powershell
kubectl get pods -n oficina
kubectl describe deployment oficina-api -n oficina
kubectl logs deployment/oficina-api -n oficina --tail=200
```

Verifique primeiro falhas de pull da imagem, conexão com RDS e probes de saúde.
