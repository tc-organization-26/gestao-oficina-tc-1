# Arquitetura da Oficina API

[Voltar ao README principal](../README.md)

## Contexto do desafio

A oficina precisa de um sistema back-end para organizar o fluxo de atendimento, diagnóstico, orçamento, execução e entrega dos veículos. O objetivo do MVP é oferecer uma base funcional, segura e documentada para que clientes, atendentes, mecânicos e gestores acompanhem o ciclo completo de uma ordem de serviço.

O sistema contempla:

- Gestão de clientes.
- Gestão de veículos.
- Gestão de serviços.
- Gestão de peças e insumos com controle de estoque.
- Criação e acompanhamento de ordens de serviço.
- Orçamentos com serviços e peças.
- Aprovação, recusa e ajustes de orçamento.
- Acompanhamento do status da OS.
- Autenticação com JWT para APIs administrativas.
- Documentação da API com Swagger.
- Execução local e deploy em Kubernetes.

## Clean Architecture

O projeto foi implementado como um monolito modular utilizando Clean Architecture. A ideia principal é proteger o domínio do negócio contra mudanças externas, como por exemplo tecnologias utilizadas no client ou no banco de dados.

No contexto desta API, regras como criação de ordem de serviço, aprovação de orçamento, baixa de estoque, cadastro de cliente e controle de veículos não dependem diretamente de HTTP, banco de dados, Spring, JWT ou JPA.

As dependências apontam para dentro:

```text
Frameworks e Drivers
        |
        v
Interface Adapters
        |
        v
Application / Use Cases
        |
        v
Domain
```

Essa direção permite que o domínio represente conceitos da oficina sem saber se os dados vieram de uma requisição REST, banco PostgreSQL, teste automatizado ou outro mecanismo.

## Camadas

### Domain

A camada `domain` é o núcleo da aplicação. Ela concentra conceitos estáveis do sistema e representa a linguagem do negócio.

Nesta camada ficam:

- Entidades de negócio, como `Cliente`, `Veiculo`, `Servico`, `ItemEstoque`, `OrdemServico` e `Orcamento`.
- Value Objects, como identificadores, placa de veículo, CPF/CNPJ e códigos de serviço.
- Enums que representam estados ou classificações do domínio.
- Eventos de domínio, como orçamento fechado, falta de peça e finalização de ordem de serviço.
- Exceções de domínio usadas para sinalizar violações de regra de negócio.

Essa camada não depende de Spring, JPA, controllers ou banco de dados.

### Application

A camada `application` coordena os fluxos de negócio e define o que o sistema é capaz de fazer.

Nesta camada ficam:

- Interfaces de casos de uso, como `CadastrarClienteUseCase`, `CriarOrdemServicoUseCase`, `AprovarOrcamentoUseCase`, `BaixarItemEstoqueUseCase` e `AutenticarUsuarioUseCase`.
- Application services, que implementam e coordenam os casos de uso.
- Commands usados para transportar dados de entrada.
- Gateways, que são contratos para persistência, autenticação, estoque e publicação de eventos.

Um service de aplicação pode depender de `ClienteGateway`, `VeiculoGateway` ou `OrdemServicoGateway`, mas não depende diretamente de repositories Spring Data.

### Interface Adapters

A camada `interfaceadapters` traduz o mundo externo para o formato esperado pela aplicação.

Nesta camada ficam:

- Controllers REST.
- Requests e responses da API.
- Gateways concretos.
- Mappers entre domínio e persistência.
- Listeners de eventos internos.

Um controller REST recebe JSON, valida a entrada básica, monta um command e chama um caso de uso. A regra de negócio permanece nos casos de uso e no domínio.

### Frameworks

A camada `frameworks` contém detalhes técnicos necessários para a aplicação rodar.

Nesta camada ficam:

- Entidades JPA.
- Repositórios Spring Data.
- Configurações de beans.
- Configurações de segurança.
- Filtros JWT.
- Integrações com Spring e infraestrutura.

Essa camada fica na borda porque tende a mudar com mais frequência.

## Organização dos pacotes

Cada módulo funcional segue a mesma separação:

- `domain`: entidades, value objects, enums, eventos e exceções de negócio.
- `application/usecases`: contratos dos casos de uso.
- `application/usecases/interactors`: implementações dos casos de uso.
- `application/dtos`: commands e objetos de entrada.
- `application/gateways`: interfaces para dependências externas.
- `interfaceadapters/controllers`: controllers REST e listeners.
- `interfaceadapters/presenters`: responses e objetos de saída.
- `interfaceadapters/gateways`: implementações concretas dos gateways.
- `frameworks/persistence`: entidades JPA e repositories Spring Data.
- `frameworks/config`: configurações do Spring.
- `frameworks/security`: configuração e filtros de segurança.
- `shared`: recursos compartilhados.

Módulos principais:

- `autenticacao`: login, validação de credenciais, geração e validação de JWT.
- `cliente`: cadastro, atualização, exclusão e consulta de clientes.
- `veiculo`: cadastro e vínculo de veículos a clientes.
- `servico`: catálogo de serviços oferecidos pela oficina.
- `estoque`: cadastro de peças, inclusão, baixa e controle de disponibilidade.
- `ordemservico`: abertura de OS, diagnóstico, orçamento, status, execução, pagamento e entrega.
- `shared`: exceções, eventos, entidades base e tratamento global de erros.

## Fluxo de uma requisição

1. O cliente da API envia uma requisição HTTP para um controller REST.
2. O controller converte o request em um command da camada `application`.
3. O controller chama uma interface de caso de uso.
4. O application service executa o fluxo, valida regras, usa entidades de domínio e acessa gateways.
5. O gateway concreto usa repositories Spring Data e entidades JPA.
6. O resultado volta para o controller, que monta uma response.

Esse fluxo mantém controllers focados em HTTP, casos de uso focados na intenção da aplicação, entidades focadas nas regras essenciais e frameworks focados nos detalhes técnicos.

## Por que PostgreSQL

O PostgreSQL foi escolhido por ser um banco relacional robusto e adequado para sistemas transacionais.

A escolha faz sentido porque:

- O domínio possui entidades fortemente relacionadas, como cliente, veículo, OS, serviços, peças e orçamentos.
- O banco oferece integridade referencial para proteger esses relacionamentos.
- Transações ajudam a manter consistência em operações como aprovar orçamento, baixar estoque e atualizar status da OS.
- Possui bom suporte no ecossistema Spring Data JPA e Hibernate.
- Funciona bem com Flyway para versionamento e evolução controlada do schema.
- É fácil de executar com Docker e Kubernetes.
- É uma opção sólida para evoluir do MVP para ambientes mais próximos de produção.

[Voltar ao README principal](../README.md)
