# Decisões técnicas e arquiteturais

Este diretório reúne as propostas técnicas (RFCs) e os registros de decisões arquiteturais (ADRs) da Oficina. Os documentos descrevem as escolhas presentes no código dos quatro repositórios, com contexto, benefícios, custos e limitações.

## Sobre os registros

A solução foi desenvolvida individualmente. Estes documentos foram criados retrospectivamente, com base em todos os componentes da solução. A data dos arquivos não é uma afirmação sobre quando a escolha original ocorreu.

Não houve processo de discussão em equipe registrado nestes documentos. Não são atribuídos comentários, revisões, reuniões, testes comparativos ou aprovações a terceiros. As alternativas são comparações técnicas retrospectivas; não indicam que houve protótipos ou avaliações históricas dessas opções.

Nas RFCs, **Encerrada – Aprovada** significa que a proposta descrita corresponde à solução adotada pela autora no projeto. Nos ADRs, **Aceita** indica essa adoção arquitetural. Pontos em aberto indicam possíveis evoluções e validações.

## Organização

Cada arquivo trata de uma proposta ou decisão. A numeração de RFCs e ADRs é independente. As RFCs seguem os campos título, data, status, resumo, problema, proposta técnica, impacto esperado, alternativas consideradas e pontos em aberto. Os ADRs seguem título, data, status, contexto, decisão e consequências.

Os documentos ficam centralizados no repositório da aplicação para facilitar a leitura integrada da solução. Referências aos outros repositórios usam links GitHub.

## RFCs

| Registro | Tema | Status | ADR relacionado |
| --- | --- | --- | --- |
| [RFC-0001](rfcs/0001-escolha-da-nuvem.md) | AWS como nuvem | Encerrada – Aprovada | [ADR-0001](adrs/0001-adotar-aws.md) |
| [RFC-0002](rfcs/0002-escolha-do-banco.md) | PostgreSQL no RDS | Encerrada – Aprovada | [ADR-0002](adrs/0002-adotar-postgresql-no-rds.md) |
| [RFC-0003](rfcs/0003-estrategia-de-autenticacao.md) | Autenticação com Lambda e JWT | Encerrada – Aprovada | [ADR-0003](adrs/0003-adotar-autenticacao-com-jwt-e-lambda.md) |
| [RFC-0004](rfcs/0004-execucao-da-aplicacao-no-eks.md) | Aplicação no EKS | Encerrada – Aprovada | [ADR-0006](adrs/0006-adotar-eks.md) |
| [RFC-0005](rfcs/0005-observabilidade-com-datadog.md) | Datadog para observabilidade | Encerrada – Aprovada | [ADR-0010](adrs/0010-adotar-datadog.md) |

## ADRs

| Registro | Decisão | Status |
| --- | --- | --- |
| [ADR-0001](adrs/0001-adotar-aws.md) | Adotar AWS | Aceita |
| [ADR-0002](adrs/0002-adotar-postgresql-no-rds.md) | Adotar PostgreSQL no RDS | Aceita |
| [ADR-0003](adrs/0003-adotar-autenticacao-com-jwt-e-lambda.md) | Adotar autenticação com JWT e Lambda | Aceita |
| [ADR-0004](adrs/0004-adotar-comunicacao-http-rest.md) | Adotar comunicação HTTP/REST síncrona | Aceita |
| [ADR-0005](adrs/0005-adotar-hpa.md) | Adotar HPA para a API | Aceita |
| [ADR-0006](adrs/0006-adotar-eks.md) | Executar a aplicação no EKS | Aceita |
| [ADR-0007](adrs/0007-manter-banco-e-aplicacao-em-rede-privada.md) | Manter banco e aplicação em rede privada | Aceita |
| [ADR-0008](adrs/0008-executar-flyway-pela-api.md) | Executar Flyway pela API | Aceita |
| [ADR-0009](adrs/0009-separar-repositorios-e-states-terraform.md) | Separar repositórios e states Terraform | Aceita |
| [ADR-0010](adrs/0010-adotar-datadog.md) | Adotar Datadog | Aceita |

## Manutenção

Se uma decisão arquitetural mudar, crie outro ADR, marque o anterior como **Substituída** e mantenha links nos dois sentidos. Use **Depreciada** quando a decisão deixar de fazer sentido sem substituição direta. Preserve os registros para manter o contexto e o histórico das escolhas.

O código e os guias operacionais detalham a implementação atual. Os registros explicam sua motivação e seus compromissos; não substituem instruções de implantação nem evidências de funcionamento.
