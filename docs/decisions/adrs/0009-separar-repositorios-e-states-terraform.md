# ADR-0009 — Separar repositórios e states Terraform

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

Rede, banco, autenticação e aplicação têm responsabilidades e ciclos de implantação diferentes. Um único state aumentaria a abrangência de cada operação Terraform; cópia manual de identificadores dificultaria reproduzir ambientes. A divisão foi implementada pela autora individualmente e não representa uma separação de equipes.

## Decisão

Mantemos quatro repositórios: `oficina-k8s-infra` para bootstrap, rede, EKS e entrada; `oficina-db-infra` para RDS; `oficina-auth-lambda` para login, Authorizer e rotas; e `gestao-oficina-tc-1` para API, migrations, imagem, manifests e observabilidade.

Separamos states remotos no S3 por componente e ambiente: `oficina/k8s/<env>.tfstate`, `oficina/database/<env>.tfstate`, `oficina/auth/<env>.tfstate` e `oficina/observability/<env>.tfstate`, com `<env>` igual a `hom` ou `prod`. Configuramos criptografia e lock nativo nos workflows. O bootstrap possui ciclo próprio de preparação do backend; a API usa deploy Kubernetes e não precisa de um state Terraform para suas regras Java. Quatro repositórios não significam exatamente quatro states no ciclo completo.

Publicamos contratos no SSM sob `/oficina/<env>/...`: rede e cluster pela plataforma, endpoint e credencial de referência pelo banco, e informações de entrada consumidas pela autenticação e aplicação. Guardamos valores secretos no Secrets Manager, e não nesses parâmetros de descoberta. Seguimos a ordem bootstrap → plataforma → banco → autenticação → API, com reexecução da autenticação quando necessária à instrumentação Datadog.

## Consequências

Cada componente pode ser alterado com escopo de state mais restrito. Identificadores publicados evitam sua cópia manual entre configurações.

É necessário coordenar ordem de implantação, nomes e compatibilidade dos contratos. Separar states não elimina dependências nem garante isolamento de permissões ou contas AWS. Remover parâmetros ou recursos compartilhados pode quebrar consumidores. O locking protege cada state, sem criar uma transação entre os repositórios. Mudanças de contrato exigem atualizar os consumidores e planejar destruição na ordem adequada.

Evidências: [responsabilidades integradas](../../ARQUITETURA-SOLUCAO.md), [esteira da plataforma](https://github.com/tc-organization-26/oficina-k8s-infra/blob/main/.github/workflows/terraform.yml), [esteira do banco](https://github.com/tc-organization-26/oficina-db-infra/blob/main/.github/workflows/terraform.yml), [esteira da autenticação](https://github.com/tc-organization-26/oficina-auth-lambda/blob/main/.github/workflows/cicd.yml) e [esteira da API](../../../.github/workflows/cicd.yml).
