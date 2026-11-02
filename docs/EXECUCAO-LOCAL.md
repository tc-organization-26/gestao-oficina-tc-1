# Execução local da Oficina API

[README](../README.md) · [AWS](EXECUCAO-LAB-AWS.md) · [Problemas comuns](TROUBLESHOOTING.md)

Para preparar o computador, consulte a [instalação das ferramentas](INSTALACAO-FERRAMENTAS.md). Para executar fora do Academy/Vocareum, siga a [preparação de conta AWS comum](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/docs/CONTA-AWS-COMUM.md), incluindo as adaptações de IAM, Terraform e workflows.

Execute os comandos na raiz deste repositório. Os exemplos usam PowerShell no Windows; em Linux/macOS, use `cp .env.example .env` e `./mvnw` no lugar de `./mvnw.cmd`.

## Pré-requisitos

- Docker Desktop iniciado e Docker Compose v2.
- Acesso à internet no primeiro build para baixar imagens e dependências.
- Java 21 somente para executar Maven/Java fora do contêiner. O Maven Wrapper já está no projeto.

## API e banco com Docker Compose

```powershell
Copy-Item .env.example .env
```

Edite `.env`: substitua `POSTGRES_PASSWORD` e `JWT_SECRET` (pelo menos 32 bytes). Mantenha banco e usuário coerentes com as variáveis JDBC. Preserve um `.env` já configurado; não o versione.

```powershell
docker compose up --build -d
docker compose ps
docker compose logs -f app
```

Use `Ctrl+C` para sair do acompanhamento de logs; os contêineres continuam em execução.

O Compose aguarda o healthcheck do PostgreSQL e o Flyway aplica as migrations quando a API inicia. Aguarde a inicialização e confirme:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health/readiness
```

Resultado esperado: `status=UP`.

| Serviço | Endereço |
| --- | --- |
| API | `http://localhost:8081` |
| Swagger | `http://localhost:8081/swagger-ui/index.html` |
| OpenAPI | `http://localhost:8081/v3/api-docs` |
| PostgreSQL a partir do computador | `localhost:5433` |
| PostgreSQL entre contêineres | `postgres:5432` |

## Login e chamada protegida

O Java usa o usuário fictício definido no código (`admin` / `ad@456`), para desenvolvimento e demonstração. Esse login recebe `login` e `senha`; o login por `cpf` é atendido pela Lambda no API Gateway da AWS.

```powershell
$baseUrl = "http://localhost:8081"
$login = Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/login" `
  -ContentType application/json `
  -Body (@{ login = "admin"; senha = "ad@456" } | ConvertTo-Json)
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "$baseUrl/clientes" -Headers $headers
```

Também é possível usar **Authorize** no Swagger com o token retornado. O CPF fictício `003.619.370-40` é inserido pelo Flyway para o fluxo da Lambda, mas não substitui as credenciais do login Java.

Para demonstrar o atendimento até pagamento e entrega, siga o [roteiro do fluxo completo no Insomnia](VALIDACAO-FLUXO.md). Ajuste o login da pasta **FLUXO COMPLETO** para o corpo Java antes de usar **Run folder** localmente.

## Java na máquina e banco no Docker

Com `.env` preparado, suba somente o banco e defina as variáveis da aplicação no terminal. O comando Java não carrega `.env` automaticamente:

```powershell
docker compose up -d postgres
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5433/oficina_db_2"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:POSTGRES_PASSWORD = "MESMO_VALOR_DO_ENV"
$env:JWT_SECRET = "MESMO_JWT_SECRET_DO_ENV_COM_32_BYTES_OU_MAIS"
./mvnw.cmd spring-boot:run
```

Pare o serviço `app` com `docker compose stop app` se ele já estiver usando `8081`. Caso tenha alterado nome do banco ou usuário em `.env`, ajuste também os valores acima.

## Build e testes

Os testes de integração precisam do PostgreSQL em `127.0.0.1:5433`. O Surefire importa `.env` conforme o `pom.xml`; mantenha senha/usuário consistentes ou use as variáveis do terminal da seção anterior.

```powershell
docker compose up -d postgres
./mvnw.cmd clean verify
```

O ciclo inclui testes unitários, integração e verificação de cobertura. O relatório fica em `target/site/jacoco/index.html`. Para construir apenas a imagem:

```powershell
docker build -t oficina-api:local .
```

## Variáveis da aplicação

| Variável | Uso | Exemplo local |
| --- | --- | --- |
| `SERVER_PORT` | Porta HTTP | `8081` |
| `SPRING_DATASOURCE_URL` | JDBC do PostgreSQL | `jdbc:postgresql://postgres:5432/oficina_db_2` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` |
| `POSTGRES_PASSWORD` | Senha do banco | Definida em `.env` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia do Hibernate | `none` |
| `SPRING_FLYWAY_BASELINE_ON_MIGRATE` | Habilita baseline | `true` |
| `SPRING_FLYWAY_BASELINE_VERSION` | Versão do baseline | `0` |
| `SPRING_FLYWAY_VALIDATE_ON_MIGRATE` | Valida migrations | `true` |
| `SECURITY_JWT_SECRET` ou `JWT_SECRET` | Assinatura HS256 | Chave com pelo menos 32 bytes |
| `SECURITY_JWT_EXPIRATION_SECONDS` | Validade do token | `3600` |

Não versione o arquivo `.env`.

O Compose lê também `POSTGRES_DB`, `POSTGRES_USER` e `JWT_EXPIRATION_SECONDS` do `.env`. Ele repassa a validade como `SECURITY_JWT_EXPIRATION_SECONDS`; no Java direto, use esse último nome. Veja [.env.example](../.env.example) e [docker-compose.yml](../docker-compose.yml).

## Encerrar e reiniciar

```powershell
docker compose down
```

Os dados permanecem no volume. Para reiniciar com banco vazio, o comando abaixo apaga os dados locais deste Compose:

```powershell
docker compose down --volumes
docker compose up --build -d
```

Novas alterações do schema devem ser migrations em `src/main/resources/db/migration`; consulte o [modelo relacional](modelo-relacional/README.md).
