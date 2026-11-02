# ADR-0002 — Adotar PostgreSQL no Amazon RDS

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

Clientes, veículos, orçamentos, peças e ordens de serviço possuem relações e precisam de persistência além do ciclo dos Pods. A aplicação usa JPA/JDBC e migrations SQL PostgreSQL. A autora implementou essa escolha individualmente; este registro retrospectivo não atribui avaliação histórica ou aprovação a terceiros.

A [RFC-0002](../rfcs/0002-escolha-do-banco.md) apresenta a proposta e as alternativas em comparação retrospectiva.

## Decisão

Persistimos os dados no RDS PostgreSQL 16, em rede privada, com armazenamento criptografado e senha master gerenciada pelo RDS no Secrets Manager. Permitimos conexões pelos Security Groups dos nós EKS e da Lambda de login. Publicamos endpoint e identificadores no SSM.

Mantemos o provisionamento da instância no repositório de banco e a evolução do schema nas migrations Flyway da API. Usamos Single-AZ e retenção de backup de sete dias por padrão, com proteção contra exclusão e snapshot final previstos na configuração.

## Consequências

As relações e transações do PostgreSQL atendem ao modelo de persistência. O RDS reduz tarefas de administração do servidor, mas exige acompanhar conexões, índices, capacidade, permissões e recuperação.

API e login compartilham banco e schema, criando dependência operacional e de compatibilidade. O banco tem custo mesmo ocioso, e Single-AZ não oferece standby em outra zona. A existência de backup não comprova restauração. Transações de persistência não implicam atomicidade de todo o caso de uso; a criação de OS atual possui commits separados. Dimensionamento e uso de credenciais com menor privilégio precisam ser avaliados conforme a evolução.

Evidências: [modelo relacional](../../modelo-relacional/README.md), [configuração RDS](https://github.com/tc-organization-26/oficina-db-infra/blob/main/main.tf) e [migrations](../../../src/main/resources/db/migration).
