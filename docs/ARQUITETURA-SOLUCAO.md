# Arquitetura da solução

[README](../README.md) · [Referência da API](API.md)

Esta seção concentra a visão integrada dos quatro repositórios. A [arquitetura do código](ARQUITETURA-APLICACAO.md) da aplicação detalha as camadas Java; o [guia Datadog](OBSERVABILIDADE.md) contém configuração, dashboards e validação da coleta.

## Diagrama de componentes da aplicação

```mermaid
flowchart TB
    consumidor["Consumidor da API"]
    uptime["UptimeRobot · monitor externo"]
    datadog["Datadog SaaS · APM, logs, métricas, dashboards e alertas"]
    subgraph aws["AWS · ambiente hom ou prod"]
        gateway["API Gateway HTTP API · HTTPS público"]
        authorizer["Lambda Authorizer · fora da VPC"]
        secrets["Secrets Manager · credenciais RDS, JWT e chave Datadog"]
        ssm["SSM Parameter Store · identificadores de infraestrutura"]
        cloudwatch["CloudWatch Logs · logs das Lambdas"]
        subgraph vpc["VPC · duas zonas de disponibilidade"]
            nat["NAT Gateway · saída das sub-redes privadas"]
            subgraph privadas["Sub-redes privadas"]
                login["Lambda login · Python / pg8000"]
                link["VPC Link"]
                nlb["NLB interno · TCP 80"]
                rds[("RDS PostgreSQL 16 · porta 5432")]
                subgraph eks["EKS · Managed Node Group"]
                    service["Service NodePort · 30081 → 8081"]
                    api["Oficina API · Spring Security, REST, casos de uso e JPA"]
                    flyway["Flyway · migrations na inicialização"]
                    agent["Datadog Operator, Agent e Cluster Agent · tracer Java SSI"]
                    escala["Metrics Server, HPA e Cluster Autoscaler"]
                end
            end
        end
    end
    consumidor --> gateway
    gateway -->|POST /auth/login público| login
    gateway -->|Autorizar rotas protegidas| authorizer
    authorizer -.->|Lê chave JWT| secrets
    gateway -->|Integração da API após autorização ou health público| link
    link --> nlb --> service --> api
    login -->|Consulta cliente ativo via TLS| rds
    login -.->|Lê credenciais e chave JWT| secrets
    api -->|JDBC| rds
    flyway -->|Versiona schema| rds
    api -.->|Logs, traces HTTP e JDBC| agent
    escala -.->|Escala Pods e nós| api
    agent -->|HTTPS de saída| nat
    login -.->|Acesso a serviços AWS e telemetria| nat
    nat --> datadog
    authorizer -.->|Layers Datadog quando habilitadas| datadog
    login -.-> cloudwatch
    authorizer -.-> cloudwatch
    uptime -->|GET público /actuator/health/readiness| gateway
    cicd["GitHub Actions · build, Terraform e deploy"]
    ghcr["GHCR · imagem da API por SHA"]
    state["S3 · states Terraform criptografados e locking"]
    cicd --> ghcr
    ghcr -.->|Imagem usada no Deployment| api
    cicd -.->|Descoberta e configuração de Secrets Kubernetes| ssm
    cicd -.->|Lê e publica secrets conforme cada workflow| secrets
    cicd -.->|Provisiona AWS, aplica Kubernetes e configura Datadog| aws
    cicd --> state
```

As setas contínuas representam chamadas e transferências; as tracejadas, configuração, coleta e controle. O Authorizer devolve a decisão ao Gateway; quem encaminha a chamada à API é a integração VPC Link. RDS, NLB, nós e Pods não possuem entrada pública da aplicação. O endpoint administrativo do EKS é público para o deploy pelo Actions.

## Contratos, segurança e persistência

- **Entrada:** `POST /auth/login`, `GET /actuator/health` e `GET /actuator/health/readiness` são públicos no Gateway. As rotas genéricas `ANY /` e `ANY /{proxy+}` exigem autorização `CUSTOM`, inclusive para Swagger/OpenAPI.
- **Identidade:** a Lambda normaliza e valida o CPF, consulta `cliente.cpf_cnpj` e exige cliente ativo. Emite JWT HS256 com `sub` igual ao UUID, `papel=CLIENTE`, `iat` e `exp` (3600 segundos por padrão). O Authorizer e o filtro Spring validam o token com a mesma chave. O login por CPF é o mecanismo de demonstração atual; não comprova posse de senha ou segundo fator.
- **Autorização atual:** o Spring exige autenticação nas rotas de negócio, sem regras por papel ou comparação automática entre `sub` e `clienteId` do corpo. O diagrama não pressupõe isolamento de dados por cliente.
- **Dados:** a API e a Lambda de login compartilham o RDS. O repositório do banco libera o SG dos nós; o da autenticação acrescenta a regra para seu próprio SG. Somente a API executa migrations Flyway. A senha do RDS é gerenciada no Secrets Manager; o workflow da API copia a configuração necessária para ConfigMap/Secret Kubernetes.
- **Integração dos repositórios:** SSM publica rede, cluster, entrada HTTP e localização do banco/secret. Os states são separados por componente e ambiente no S3; `homologacao` usa `hom` e `main` usa `prod`.
- **Escala e disponibilidade:** HPA mantém 2–6 réplicas por CPU/memória; Cluster Autoscaler ajusta os nós (1–4 por padrão). O RDS usa `multi_az=false` por padrão; duas AZs na rede não significam standby do banco. Backups têm retenção padrão de sete dias e snapshot final na remoção.

O schema disponibiliza o CPF fictício `003.619.370-40` (`00361937040` no banco), UUID `00000000-0000-0000-0000-000000000001` como cliente ativo para testes. A API também oferece cadastro de clientes. Aguarde o startup com Flyway antes de testar o login.

## Sequência de autenticação

```mermaid
sequenceDiagram
    autonumber
    actor Cliente
    participant Gateway as API Gateway
    participant Login as Lambda login
    participant Secrets as Secrets Manager
    participant DB as RDS PostgreSQL
    Cliente->>Gateway: POST /auth/login {cpf}
    Gateway->>Login: Integração AWS_PROXY (sem JWT)
    Login->>Login: Normalizar CPF e validar dígitos
    alt Corpo ou CPF inválido
        Login-->>Gateway: 400
        Gateway-->>Cliente: 400
    else CPF válido
        Login->>Secrets: Ler credencial RDS (cache por instância)
        Secrets-->>Login: username e password
        Login->>DB: SELECT id, ativo FROM cliente WHERE cpf_cnpj = %s (TLS)
        DB-->>Login: Cliente ou nenhum registro
        alt Cliente inexistente ou inativo
            Login-->>Gateway: 404 inexistente / 403 inativo
            Gateway-->>Cliente: Erro de autenticação
        else Cliente ativo
            Login->>Secrets: Ler jwtSecret (cache por instância)
            Secrets-->>Login: Chave HS256
            Login->>Login: Assinar JWT com UUID e papel CLIENTE
            Login-->>Gateway: 200 {token} e X-Request-ID
            Gateway-->>Cliente: JWT para chamadas protegidas
        end
    end
    Note over Login,DB: Falhas inesperadas de dependência retornam 500 e geram logs de erro.
```

Contrato completo e exemplos de erros estão na [referência da autenticação](https://github.com/tc-organization-26/oficina-auth-lambda/blob/HEAD/README.md#acesso-à-api). Neste repositório, `POST /auth/login` é atendido pelo Java e recebe `{login, senha}`; a Lambda por CPF é a integração usada na nuvem.

## Sequência de abertura de ordem de serviço

Pré-condições: migrations aplicadas, cliente e veículo cadastrados e códigos de serviços/peças existentes quando informados. O corpo recebe `clienteId`, `veiculoId`, listas obrigatórias `servicos` e `pecas` (podem ser vazias) e `anotacoes` opcional. Cada item contém `codigo` e `quantidade` maior ou igual a `0.001`.

```mermaid
sequenceDiagram
    autonumber
    actor Cliente
    participant Gateway as API Gateway
    participant Auth as Lambda Authorizer
    participant Rede as VPC Link / NLB / NodePort
    participant API as Spring Security / Controller REST
    participant UC as CriarOrdemServicoUseCase
    participant Persist as Gateways JPA e histórico
    participant DB as RDS PostgreSQL
    participant Obs as Logs / Datadog
    Cliente->>Gateway: POST /ordens-servico + Bearer JWT + JSON
    Gateway->>Auth: Validar JWT com chave do Secrets Manager
    Auth-->>Gateway: isAuthorized
    alt Token ausente, inválido ou expirado
        Gateway-->>Cliente: Rejeição de acesso (sem chamar a API)
    else Token aceito
        Gateway->>Rede: Encaminhar requisição e Authorization
        Rede->>API: HTTP na porta 8081
        API->>API: Validar JWT novamente e validar DTO
        API->>UC: criar(command)
        UC->>UC: Criar OS RECEBIDA e orçamento inicial
        UC->>Persist: salvar(ordemServico)
        Persist->>DB: Persistir OS e histórico na transação
        DB-->>Persist: Commit, UUID e número da OS
        Persist-->>Obs: os.created após commit
        Persist-->>UC: Ordem salva
        loop Serviços e peças informados
            UC->>Persist: Buscar código e verificar estoque das peças
            Persist->>DB: Consultar catálogo / estoque
            DB-->>UC: Dados dos itens
            UC->>UC: Adicionar item ao orçamento
            opt Peça sem disponibilidade
                UC-->>Obs: Evento interno FaltaPecaEstoqueEvent (listener de log)
            end
        end
        UC->>Persist: salvar(orcamento) e buscar OS por id
        Persist->>DB: Persistir orçamento e consultar OS
        DB-->>UC: OS com orçamento
        UC-->>API: OrdemServico
        API-->>Rede: 201 Created, Location e {id, numero}
        Rede-->>Gateway: Resposta HTTP
        Gateway-->>Cliente: 201 Created
        API-->>Obs: Log HTTP com status, duração e request_id, com trace quando ativo
    end
```

O diagrama mostra o caminho de sucesso após a validação. DTO inválido gera `400`; código inexistente ou outra `DomainException` gera `422`; falhas técnicas podem gerar `500`. A indisponibilidade de peça publica um evento, sem bloquear automaticamente a abertura ou efetuar a baixa do estoque nessa etapa.

## Monitoramento e limites operacionais

Na aplicação, o Datadog acompanha logs, traces e métricas para ajudar a observar requisições, saúde da aplicação e acesso ao PostgreSQL. Os endpoints de healthcheck mostram se a API está disponível. Consulte o [guia operacional de observabilidade](OBSERVABILIDADE.md) para mais informações.
