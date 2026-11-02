# ADR-0001 — Adotar AWS como nuvem

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

A Oficina precisa disponibilizar a API, preservar os dados e permitir expansão de capacidade com ambientes reproduzíveis. O projeto individual utiliza laboratório AWS, com credenciais temporárias e limitações de IAM. O registro é retrospectivo: a data indica sua criação, e o status representa a adoção no código, sem discussão ou aprovação de terceiros.

A [RFC-0001](../rfcs/0001-escolha-da-nuvem.md) reúne a proposta e uma comparação técnica retrospectiva das alternativas.

## Decisão

Adotamos AWS para executar a solução integrada. Usamos EKS para a API, RDS para PostgreSQL, Lambda e API Gateway para autenticação e entrada, VPC para rede, Secrets Manager para credenciais, SSM Parameter Store para compartilhar configurações e identificadores entre os componentes e S3 para states Terraform.

Provisionamos os componentes por Terraform e workflows GitHub Actions, separando `hom` e `prod`. Mantemos a imagem no GHCR e a observabilidade no Datadog. No laboratório, usamos as permissões e credenciais temporárias previstas nos guias; a migração para uma conta comum exige configurar IAM e autenticação adequados à nova conta.

## Consequências

O provisionamento em código permite reproduzir a composição da infraestrutura e explicitar dependências. A integração com os serviços AWS reduz a necessidade de implementar determinados mecanismos de infraestrutura na aplicação.

A escolha gera dependência de APIs e recursos AWS, custos recorrentes e necessidade de administrar permissões e conectividade. NAT único e RDS Single-AZ limitam a disponibilidade padrão; duas zonas na VPC não eliminam essas limitações. O ambiente de laboratório depende da sessão e das restrições de acesso. A adoção não comprova implantação ativa nem recuperação validada.

Evidências: [arquitetura integrada](../../ARQUITETURA-SOLUCAO.md) e [infraestrutura AWS](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/README.md).
