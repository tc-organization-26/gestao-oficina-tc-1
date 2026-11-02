# Sobre a API da Oficina

[README](../README.md) · [Tecnologias e versões](VERSOES.md) · [Execução local](EXECUCAO-LOCAL.md) · [Execução no laboratório AWS](EXECUCAO-LAB-AWS.md)

## Funcionalidades

- cadastro, consulta, atualização e exclusão de clientes;
- pesquisa de cliente por CPF ou CNPJ;
- cadastro e consulta de veículos vinculados ao cliente;
- catálogo de serviços da oficina;
- cadastro e movimentação de peças e insumos do estoque;
- abertura e acompanhamento de ordens de serviço;
- registro de diagnóstico;
- composição de orçamento com serviços e peças;
- aprovação ou recusa de orçamento;
- registro de pagamento;
- acompanhamento do tempo médio de execução.

À medida que as chamadas à API REST são executadas, os status da ordem de serviço são alterados, sendo eles:

- `Recebida`: atendimento registrado;
- `Em diagnóstico`: veículo em avaliação;
- `Aguardando aprovação`: orçamento aguardando decisão do cliente;
- `Em execução`: orçamento aprovado e serviço iniciado;
- `Finalizada`: serviços concluídos;
- `Entregue`: pagamento e retirada concluídos.

## Rotas

O catálogo abaixo reúne todos os métodos e caminhos declarados nos controllers da aplicação. Os caminhos são relativos à URL base: `http://localhost:8081` no Compose ou a URL publicada pelo API Gateway na AWS.

Os parâmetros `{id}`, `{ordemId}`, `clienteId` e `veiculoId` identificam UUIDs; `{codigo}` identifica um código de catálogo e `{cpfCnpj}` um documento.

As rotas de negócio exigem `Authorization: Bearer TOKEN`. O login é público. Na AWS, o Authorizer verifica o JWT antes de encaminhar as chamadas à API; a aplicação também valida o token. As regras das rotas técnicas estão descritas ao final.

### Autenticação

Fonte: [AuthController](../src/main/java/br/com/fiap/oficina/autenticacao/interfaceadapters/controllers/rest/AuthController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/auth/login` | Gerar JWT. Diretamente no Java, recebe `login` e `senha`; pelo Gateway AWS, a Lambda recebe `cpf`, consulta o cliente ativo no RDS e emite o token. |

O [guia local](EXECUCAO-LOCAL.md#login-e-chamada-protegida) contém o login Java. O [contrato da autenticação AWS](https://github.com/tc-organization-26/oficina-auth-lambda/blob/main/README.md#acesso-à-api) descreve o login por CPF, seus erros e a collection Insomnia correspondente.

### Clientes

Fonte: [ClienteController](../src/main/java/br/com/fiap/oficina/cliente/interfaceadapters/controllers/rest/ClienteController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/clientes` | Cadastrar cliente com nome, CPF/CNPJ e contatos. |
| `GET` | `/clientes` | Listar clientes. |
| `GET` | `/clientes/{id}` | Consultar cliente pelo UUID. |
| `GET` | `/clientes/documento/{cpfCnpj}` | Consultar cliente por CPF ou CNPJ. |
| `PUT` | `/clientes/{id}` | Atualizar nome, email e telefone do cliente. |
| `DELETE` | `/clientes/{id}` | Excluir cliente conforme as regras de negócio e integridade referencial. |
| `GET` | `/clientes/{id}/veiculos` | Listar os veículos vinculados ao cliente. |

### Veículos

Fonte: [VeiculoController](../src/main/java/br/com/fiap/oficina/veiculo/interfaceadapters/controllers/rest/VeiculoController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/veiculos` | Cadastrar veículo vinculado a um cliente, informando placa, marca, modelo e ano. |
| `GET` | `/veiculos` | Listar veículos. |
| `GET` | `/veiculos/{id}` | Consultar veículo pelo UUID. |
| `PUT` | `/veiculos/{id}` | Atualizar marca, modelo e ano do veículo. |
| `DELETE` | `/veiculos/{id}` | Excluir veículo conforme as regras de negócio e integridade referencial. |

### Serviços

Fonte: [ServicoController](../src/main/java/br/com/fiap/oficina/servico/interfaceadapters/controllers/rest/ServicoController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/servicos` | Cadastrar serviço com código, descrição, valor unitário e tempo estimado em minutos. |
| `GET` | `/servicos` | Listar serviços. |
| `GET` | `/servicos/{id}` | Consultar serviço pelo UUID. |
| `PUT` | `/servicos/{id}` | Atualizar descrição, valor unitário e tempo estimado. |
| `DELETE` | `/servicos/{id}` | Excluir serviço conforme as regras de negócio e integridade referencial. |

### Estoque

Fonte: [EstoqueController](../src/main/java/br/com/fiap/oficina/estoque/interfaceadapters/controllers/rest/EstoqueController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/estoque` | Cadastrar peça ou insumo com código, descrição, valor unitário e quantidade inicial. |
| `GET` | `/estoque` | Listar itens de estoque. |
| `GET` | `/estoque/{id}` | Consultar item pelo UUID. |
| `GET` | `/estoque/codigo/{codigo}` | Consultar item pelo código. |
| `GET` | `/estoque/consulta/ativos` | Listar itens ativos. |
| `PUT` | `/estoque/{id}` | Atualizar descrição e valor unitário do item. |
| `DELETE` | `/estoque/{id}` | Excluir item conforme as regras de negócio e integridade referencial. |
| `POST` | `/estoque/{id}/inclusoes` | Acrescentar estoque ao item identificado pelo UUID; corpo com `quantidade`. |
| `POST` | `/estoque/{codigo}/baixas` | Baixar estoque do item identificado pelo código; corpo com `quantidade`. |

A inclusão usa o UUID do item, enquanto a baixa usa seu código de catálogo.

### Ordens de serviço

Fonte: [OrdemServicoController](../src/main/java/br/com/fiap/oficina/ordemservico/interfaceadapters/controllers/rest/OrdemServicoController.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `POST` | `/ordens-servico` | Abrir OS com `clienteId`, `veiculoId`, listas `servicos` e `pecas`, e `anotacoes` opcionais. Retorna `id`, `numero` e cabeçalho `Location`. |
| `GET` | `/ordens-servico` | Listar ordens, com filtros opcionais de query `status` e `clienteId`. |
| `GET` | `/ordens-servico/tempo-medio-execucao` | Consultar o tempo médio de execução; resposta com o campo `tempoMedioExecucao`. |
| `GET` | `/ordens-servico/{id}` | Consultar os dados da OS, incluindo seu orçamento. |
| `GET` | `/ordens-servico/{id}/status` | Consultar o status da OS. |
| `POST` | `/ordens-servico/{id}/diagnostico` | Registrar o diagnóstico; corpo com `descricao`. |
| `POST` | `/ordens-servico/{ordemId}/orcamento/servicos` | Adicionar serviço ao orçamento; corpo com `codigo` e `quantidade`. |
| `POST` | `/ordens-servico/{ordemId}/orcamento/pecas` | Adicionar peça ao orçamento; corpo com `codigo` e `quantidade`. |
| `POST` | `/ordens-servico/{ordemId}/orcamento/notificacoes-aprovacao` | Registrar decisão do orçamento; corpo com `decisao` (`APROVADO` ou `RECUSADO`), `origem` e `protocoloExterno` opcionais. |
| `POST` | `/ordens-servico/{ordemId}/orcamento/recusa` | Recusar o orçamento da OS, sem corpo de requisição. |
| `POST` | `/ordens-servico/{ordemId}/pagamento` | Registrar o pagamento da OS, sem corpo de requisição. |
| `PATCH` | `/ordens-servico/{ordemId}/status` | Atualizar o status conforme as transições permitidas; corpo com `status`. |

Os nomes `{id}` e `{ordemId}` acima seguem os controllers e se referem ao UUID da mesma entidade. Na abertura, `servicos` e `pecas` são obrigatórios, mas podem ser listas vazias; cada item informado contém `codigo` e `quantidade` de pelo menos `0.001`.

Os valores de `status`, tanto no filtro quanto na atualização, são `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`. Os filtros podem ser combinados, por exemplo: `/ordens-servico?clienteId=UUID_DO_CLIENTE&status=EM_EXECUCAO`. O status solicitado continua sujeito às regras de transição do domínio.

### Saúde e informações operacionais

Configuração: [application.properties](../src/main/resources/application.properties) e [SecurityConfig](../src/main/java/br/com/fiap/oficina/autenticacao/frameworks/security/SecurityConfig.java).

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `GET` | `/actuator` | Índice dos endpoints Actuator expostos. |
| `GET` | `/actuator/health` | Estado geral de saúde da aplicação. |
| `GET` | `/actuator/health/liveness` | Verificação de que a aplicação está viva, usada pela probe de liveness. |
| `GET` | `/actuator/health/readiness` | Verificação de prontidão para receber tráfego, incluindo o banco. |
| `GET` | `/actuator/info` | Informações publicadas pelo Actuator; o conteúdo depende dos contributors configurados. |

No acesso direto à aplicação, os endpoints de saúde são públicos; `/actuator` e `/actuator/info` exigem JWT. Pelo Gateway AWS, somente `GET /actuator/health` e `GET /actuator/health/readiness` têm rotas públicas explícitas. O acesso aos demais caminhos passa pelo Authorizer. Detalhes internos de saúde não são expostos (`show-details=never`).

### Swagger e OpenAPI

| Método | Caminho | Finalidade |
| --- | --- | --- |
| `GET` | `/swagger-ui.html` | Entrada alternativa para a interface Swagger. |
| `GET` | `/swagger-ui/index.html` | Interface interativa Swagger UI. |
| `GET` | `/v3/api-docs` | Especificação OpenAPI em JSON. |
| `GET` | `/v3/api-docs/swagger-config` | Configuração utilizada pela Swagger UI. |
| `GET` | `/v3/api-docs.yaml` | Especificação OpenAPI em YAML. |

No acesso direto, a configuração Spring libera `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs` e `/v3/api-docs/**`. O caminho `/v3/api-docs.yaml` exige JWT porque não está nesses matchers. Pelo Gateway AWS, Swagger e OpenAPI exigem JWT. Os arquivos estáticos da interface são carregados sob `/swagger-ui/**`.

### Contratos e exemplos

As criações de cliente, veículo, serviço, item de estoque e OS retornam `201 Created`; exclusões retornam `204 No Content`. Os demais handlers dos controllers retornam `200 OK` quando concluídos com sucesso. Os endpoints de saúde podem retornar outro status conforme o estado da aplicação e das dependências.

Consulte os schemas de entrada e saída no [Swagger local](http://localhost:8081/swagger-ui/index.html) e a [collection Insomnia](../src/main/resources/insomnia/collection.yaml).

O [roteiro de validação do fluxo completo](VALIDACAO-FLUXO.md) explica a preparação da collection, a execução com **Run folder**, os resultados esperados e os cenários de recusa, revisão de orçamento e movimentação de estoque.
