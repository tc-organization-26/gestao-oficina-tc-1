# ADR-0007 — Manter banco e aplicação em rede privada

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

O acesso público deve passar pela entrada HTTP e pelos controles de autenticação, enquanto banco e workloads precisam comunicar-se internamente. A operação também exige saída para GHCR, serviços AWS e Datadog. Este é um registro retrospectivo da arquitetura implementada individualmente.

## Decisão

Mantemos nós e Pods da API, RDS, NLB interno e Lambda de login em sub-redes privadas. Expomos a aplicação pelo API Gateway público em HTTPS, com integração VPC Link → NLB → NodePort `30081` → API `8081`. O NLB usa TCP na porta 80; a presença de HTTPS no Gateway não implica criptografia ponta a ponta em todo o caminho interno.

Configuramos o RDS como não público e permitimos PostgreSQL por Security Groups dos nós EKS e da Lambda de login. O repositório de plataforma controla VPC, sub-redes e entrada; o de banco controla seu SG e acesso dos nós; o de autenticação acrescenta seu SG e a regra de conexão ao banco. A aplicação mantém Service, portas e probes compatíveis com essa integração.

Usamos NAT Gateway para a saída das sub-redes privadas. O Authorizer fica fora da VPC. O endpoint administrativo do EKS permanece público para a esteira; o isolamento do tráfego de negócio não significa que todo endpoint de gerenciamento seja privado.

## Consequências

A entrada fica concentrada no Gateway, e o banco não recebe conexões diretamente da internet. Diagnóstico e administração exigem caminhos autorizados até a rede privada.

A solução depende de rotas, Security Groups e integração coerentes entre repositórios. NAT único implica custo e limitação de disponibilidade para saída. Sub-redes privadas não substituem autorização da aplicação, proteção de segredos ou criptografia. Acesso administrativo EKS e eventual necessidade de TLS interno precisam ser revistos conforme o ambiente de uso.

Evidências: [arquitetura integrada](../../ARQUITETURA-SOLUCAO.md), [entrada da plataforma](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/edge.tf) e [rede do banco](https://github.com/tc-organization-26/oficina-db-infra/blob/main/main.tf).
