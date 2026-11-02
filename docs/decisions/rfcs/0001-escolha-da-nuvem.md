# RFC-0001 — Adoção da AWS como nuvem

- **Data:** 02/10/2026
- **Status:** Encerrada – Aprovada

## Resumo

Concentrar a implantação da Oficina na AWS, integrando execução da API, persistência, autenticação e rede com infraestrutura Terraform reproduzível.

## Problema

A oficina precisa de uma API acessível para sustentar cadastro, diagnóstico, orçamento, execução e entrega, com capacidade de acompanhar o aumento da demanda. Uma instalação dependente de configuração manual dificulta reproduzir ambientes e recuperar recursos. O ambiente educacional usa credenciais temporárias e restrições de IAM que precisam ser consideradas no provisionamento.

## Proposta técnica

Adotar AWS para a implantação integrada: EKS para a API, RDS PostgreSQL para dados, Lambda para login e autorização, API Gateway para entrada, VPC para isolamento, Secrets Manager para credenciais e SSM para descoberta de recursos. Usar S3 como backend dos states Terraform, com criptografia e locking configurados pelos workflows.

Separar homologação (`hom`) e produção (`prod`) por configuração, recursos e chaves de state. GitHub Actions conduz os deploys; a imagem permanece no GHCR. A escolha da nuvem não exige que todos os serviços externos pertençam à AWS: a observabilidade usa Datadog.

No laboratório, utilizar a LabRole e credenciais temporárias conforme os guias. A execução em uma conta comum exige adaptação de IAM e autenticação dos workflows; a existência de bootstrap OIDC não comprova que ele esteja ativo no laboratório.

## Impacto esperado

O conjunto de serviços permite provisionamento integrado e repetível, com responsabilidades definidas por componente. Em contrapartida, amplia a dependência de serviços e contratos específicos da AWS e exige administrar permissões, conectividade e custos de EKS, nós, RDS, NAT, tráfego e armazenamento.

A rede usa duas zonas de disponibilidade, mas o padrão atual tem NAT único e RDS Single-AZ. Esses parâmetros restringem disponibilidade e não devem ser apresentados como tolerância completa à perda de uma zona. Recursos no laboratório dependem da validade da sessão e das permissões disponíveis.

## Alternativas consideradas

Esta comparação é retrospectiva, sem testes comparativos registrados. Azure e Google Cloud poderiam hospedar componentes equivalentes, mas exigiriam adaptar os módulos, contratos e integrações AWS existentes. Uma implantação apenas local com Docker Compose facilita desenvolvimento e reduz dependências de nuvem, mas não atende à implantação integrada AWS e aos mecanismos de escala descritos. Manter servidores virtuais manualmente reduziria algumas abstrações, transferindo mais responsabilidades operacionais à autora.

## Pontos em aberto

- Definir orçamento, alertas de gasto e dimensionamento a partir de uso medido.
- Validar recuperação, impacto do NAT único e necessidade de RDS Multi-AZ fora do laboratório.
- Adaptar IAM e autenticação dos workflows para uma conta comum antes dessa migração.

Registro retrospectivo individual, conforme a [convenção dos documentos](../README.md). Decisão consolidada no [ADR-0001](../adrs/0001-adotar-aws.md). Evidências: [arquitetura da solução](../../ARQUITETURA-SOLUCAO.md) e [plataforma AWS](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/README.md).
