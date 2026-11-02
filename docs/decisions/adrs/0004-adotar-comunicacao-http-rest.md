# ADR-0004 — Adotar comunicação HTTP/REST síncrona

- **Data:** 02/10/2026
- **Status:** Aceita

## Contexto

O atendimento da Oficina exige consultar cadastros e receber resposta às operações de abertura de OS, composição de orçamento e mudança de status. A solução atual concentra regras de negócio em uma API Java, sem serviços de negócio distribuídos que exijam mensageria. Este registro descreve retrospectivamente a implementação da autora, sem afirmar que houve discussão em equipe.

## Decisão

Expomos a interface de negócio por HTTP/REST síncrono, com representações JSON e contrato OpenAPI. Encaminhamos as chamadas pelo API Gateway, VPC Link, NLB e Service até a aplicação. O consumidor aguarda a resposta da requisição; a aplicação acessa PostgreSQL por JDBC, não por REST.

Mantemos os eventos internos da aplicação como mecanismos no próprio processo. Eles não constituem uma fila externa, um broker ou garantia de entrega durável. Consideraremos mensageria quando surgirem necessidades concretas de processamento demorado, integração independente, absorção de picos ou retentativas duráveis, por exemplo para notificações externas.

## Consequências

O consumidor recebe uma resposta direta e pode usar ferramentas HTTP e a documentação existente para executar o fluxo. A implementação evita a operação adicional de um broker no cenário atual.

A requisição depende da disponibilidade e latência do caminho até a API e do banco. Timeouts e repetições de chamadas de escrita exigem cuidado para evitar duplicação; o protocolo não garante idempotência de todas as operações. Uma evolução assíncrona exigirá contratos de eventos, tratamento de falhas, deduplicação e estratégia de consistência, sem presumir que os listeners atuais já ofereçam isso.

Evidências: [referência da API](../../API.md), [sequências da solução](../../ARQUITETURA-SOLUCAO.md) e [limites dos eventos internos](../../OBSERVABILIDADE.md).
