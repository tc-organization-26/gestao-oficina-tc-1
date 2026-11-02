# RFC-0002 — Adoção de PostgreSQL no Amazon RDS

- **Data:** 02/10/2026
- **Status:** Encerrada – Aprovada

## Resumo

Persistir os dados relacionais da Oficina no PostgreSQL gerenciado pelo RDS, separado do ciclo de vida dos Pods e com schema versionado pelo Flyway da API.

## Problema

Clientes, veículos, serviços, peças, orçamentos e ordens de serviço possuem vínculos que precisam ser preservados. A aplicação precisa de consultas relacionais e transações para suas operações de persistência. Hospedar o banco junto ao processo da API dificultaria preservar dados durante substituições e escalabilidade dos Pods.

## Proposta técnica

Usar RDS PostgreSQL 16 em sub-redes privadas, com acesso por Security Groups dos nós EKS e da Lambda de login. O repositório de banco provisiona instância, rede, armazenamento criptografado, backups e credencial master gerenciada pelo RDS no Secrets Manager. Endpoint e identificadores são publicados no SSM.

A API utiliza JPA/JDBC e executa migrations Flyway na inicialização. A Lambda de login consulta o mesmo cadastro de clientes por CPF com conexão TLS; ela não administra o schema. O banco utiliza Single-AZ e retenção de backup de sete dias por padrão, com proteção contra exclusão e snapshot final configurados no Terraform.

## Impacto esperado

O modelo relacional favorece integridade referencial e consultas entre entidades. O serviço gerenciado reduz tarefas de administração da instância, mas mantém a responsabilidade por schema, índices, consultas, permissões e recuperação dos dados.

O RDS gera custo mesmo sem tráfego. Single-AZ limita disponibilidade; backup não substitui um teste de restauração. API e login compartilham a disponibilidade e o schema do banco. A existência de transações no PostgreSQL não torna todo caso de uso atômico: o fluxo atual de criação de OS possui commits separados, conforme a documentação da aplicação.

## Alternativas consideradas

Como comparação retrospectiva, PostgreSQL autogerenciado em EC2 ou Kubernetes manteria o modelo de dados, mas acrescentaria operação de persistência, atualização e recuperação. MySQL no RDS seria uma alternativa relacional, porém exigiria revisar SQL, migrations e compatibilidade da implementação PostgreSQL. Um banco NoSQL exigiria redesenhar relações, consultas e garantias conforme o modelo escolhido. Não foram registrados benchmarks dessas opções.

## Pontos em aberto

- Dimensionar instância, armazenamento e conexões com medições de uso e carga.
- Definir objetivos de recuperação e comprovar restauração de backup.
- Avaliar Multi-AZ e usuários de banco com privilégios específicos para API e login.

Registro retrospectivo individual, conforme a [convenção dos documentos](../README.md). Decisão no [ADR-0002](../adrs/0002-adotar-postgresql-no-rds.md). Evidências: [modelo relacional](../../modelo-relacional/README.md), [infraestrutura do banco](https://github.com/tc-organization-26/oficina-db-infra/blob/main/main.tf) e [limites transacionais](../../OBSERVABILIDADE.md).
