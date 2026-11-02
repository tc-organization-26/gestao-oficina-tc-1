# ADR-0008 — Executar migrations Flyway pela API

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

O schema precisa acompanhar o código de persistência e evoluir de forma versionada. O banco é provisionado em repositório separado, e a Lambda de login depende da tabela de clientes. Centralizar a evolução SQL na aplicação evita distribuir essa responsabilidade entre componentes. A escolha já existe na implementação individual e é registrada retrospectivamente.

## Decisão

Versionamos as migrations em `src/main/resources/db/migration` e executamos Flyway na inicialização da API Spring Boot. Mantemos a geração de DDL pelo Hibernate desabilitada por padrão (`ddl-auto=none`) e a validação de migrations habilitada. O Terraform do banco provisiona infraestrutura, sem criar as tabelas de negócio; a Lambda consulta o schema existente, sem executar migrations.

Na primeira implantação, iniciamos a API e aguardamos Flyway antes de validar o login por CPF. Preservamos migrations já aplicadas e acrescentamos novas versões para alterações posteriores. O controle do Flyway no banco coordena a aplicação das versões durante inicializações concorrentes; não pressupomos que cada réplica deva executar novamente o mesmo SQL.

## Consequências

Schema e código podem ser revisados no mesmo repositório, e o histórico Flyway permite identificar versões aplicadas. A inicialização fica dependente da conectividade, permissões e sucesso das migrations. Uma falha pode impedir que os novos Pods fiquem prontos.

Reverter a imagem não desfaz mudanças no banco. Rollouts com versões coexistentes exigem migrations compatíveis com a versão anterior, preferencialmente separando expansão e remoção de estruturas. Alterações demoradas ou destrutivas exigem avaliar execução dedicada e recuperação previamente. A dependência do login em relação ao primeiro startup da API precisa ser respeitada na validação integrada.

Evidências: [migrations](../../../src/main/resources/db/migration), [configuração Spring](../../../src/main/resources/application.properties) e [ordem de implantação](../../EXECUCAO-LAB-AWS.md).
