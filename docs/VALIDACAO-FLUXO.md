# Validação do fluxo completo da oficina

[README](../README.md) · [Contrato da API](API.md) · [Execução local](EXECUCAO-LOCAL.md) · [Execução AWS](EXECUCAO-AWS.md)

Este roteiro demonstra o atendimento da identificação do cliente à entrega do veículo. Use um ambiente de teste com dados fictícios. Ele complementa o readiness e o rollout com a verificação das operações de negócio.

## Preparar o ambiente

1. Inicie API e PostgreSQL pelo [Docker Compose](EXECUCAO-LOCAL.md#api-e-banco-com-docker-compose) ou conclua a [implantação integrada na AWS](EXECUCAO-AWS.md#ordem-da-primeira-implantação).
2. Aguarde o Flyway e confirme `GET /actuator/health/readiness` com `status=UP`.
3. Importe no Insomnia a [collection da API](../src/main/docs/insomnia/collection.yaml).
4. Selecione o ambiente e revise os valores importados. A URL, o token e os UUIDs salvos na collection são exemplos de uma execução anterior; obtenha valores válidos para seu ambiente.

| Variável | Preparação |
| --- | --- |
| `BASE_URL` | `http://localhost:8081` no Compose; URL real do API Gateway na AWS; `http://localhost:18081` se usar port-forward |
| `TOKEN` | Será substituído pelo script do login; não reutilize o token exportado |
| `CLIENTE_ID` | UUID de cliente existente; o seed inicial é `00000000-0000-0000-0000-000000000001` |
| `VEICULO_PLACA` | Placa fictícia válida e ainda não cadastrada, como `ABC1D23` em um banco onde esteja livre |
| `SERVICO_COD` | Código de serviço ainda não cadastrado, por exemplo `REVISAO-DEMO-001` |
| `ITEM_ESTOQUE_CODIGO` | Código de peça ainda não cadastrado, por exemplo `FILTRO-DEMO-001` |
| `VEICULO_ID`, `SERVICO_ID`, `ITEM_ESTOQUE_ID`, `ORDEM_ID` | Atualizados pelos scripts das respectivas criações; confirme as respostas antes de continuar |

A pasta **FLUXO COMPLETO** usa o cliente já cadastrado e cria veículo, serviço, peça e OS. Não há criação de cliente nessa pasta. Para usar outro cliente, autentique primeiro com os dados de teste disponíveis, cadastre-o na pasta **Clientes**, informe seu UUID em `CLIENTE_ID` e ajuste o CPF do login AWS quando quiser usar essa identidade. `CLIENTE_DOC` pertence às requisições de cadastro/consulta de cliente; não altera sozinho o CPF literal do login.

## Ajustar a autenticação

Na requisição **Login** da pasta **FLUXO COMPLETO**, use o corpo correspondente ao destino:

| Destino | Corpo JSON |
| --- | --- |
| Gateway AWS, com cliente inicial ativo | `{"cpf":"00361937040"}` |
| Compose ou port-forward direto para o Java | `{"login":"admin","senha":"ad@456"}` |

O login deve retornar `200` com `token`. O script `afterResponse` grava esse valor em `TOKEN`, usado pelos cabeçalhos Bearer das chamadas seguintes. Se o login falhar, interrompa a execução e corrija URL, corpo ou dependências antes de criar dados.

O token AWS contém a identidade do cliente, mas a autorização atual não compara automaticamente `sub` e `CLIENTE_ID`. Mantenha os dados coerentes na demonstração; isso não representa isolamento por cliente implementado.

## Executar a pasta FLUXO COMPLETO

Selecione **Run folder** na pasta **FLUXO COMPLETO**. Antes de iniciar, confira no runner a ordem abaixo: o YAML exportado contém as requisições em ordem textual diferente da ordenação por `sortKey`. Se necessário, ajuste a sequência no runner ou execute manualmente na ordem indicada.

| Ordem | Requisição | Resultado esperado |
| --- | --- | --- |
| 1 | Login | `200`; script atualiza `TOKEN` |
| 2 | Criar veículo | `201`; vínculo com `CLIENTE_ID` e atualização de `VEICULO_ID` |
| 3 | Criar item de estoque | `201`; quantidade inicial de teste e atualização de `ITEM_ESTOQUE_ID` |
| 4 | Criar serviço | `201`; atualização de `SERVICO_ID` e `SERVICO_COD` |
| 5 | Criar ordem de serviço | `201`, cabeçalho `Location`, `id` e `numero`; script atualiza `ORDEM_ID` |
| 6 | Alterar status para `EM_DIAGNOSTICO` | `200`; avaliação iniciada |
| 7 | Adicionar serviço ao orçamento | `200`; item incluído no orçamento aberto |
| 8 | Adicionar peça ao orçamento | `200`; peça incluída e disponibilidade verificada |
| 9 | Registrar diagnóstico | `200`; descrição técnica registrada |
| 10 | Alterar status para `AGUARDANDO_APROVACAO` | `200`; orçamento fechado e enviado |
| 11 | Notificação de aprovação de orçamento | `200`; orçamento `APROVADO`, sem iniciar execução automaticamente |
| 12 | Alterar status para `EM_EXECUCAO` | `200`; execução iniciada após aprovação |
| 13 | Alterar status para `FINALIZADA` | `200`; conclusão registrada |
| 14 | Registrar pagamento | `200`; confirmação interna de pagamento |
| 15 | Alterar status para `ENTREGUE` | `200`; entrega registrada |
| 16 | Acompanhamento da ordem | `200`; consulta pelo `ORDEM_ID` com status `ENTREGUE` e pagamento registrado |

A criação da OS já inclui um serviço; a requisição posterior também adiciona serviço ao orçamento. Confira os itens e quantidades retornados, sem pressupor que a abertura começou com orçamento vazio. A baixa de estoque não faz parte dessa pasta; valide-a separadamente durante a execução, conforme a próxima seção.

O runner envia operações reais à API. Antes de repetir a pasta, escolha outra placa e outros códigos de catálogo para evitar duplicidades. Após erro ou timeout, confira o último recurso criado antes de repetir um POST; etapas anteriores podem ter sido persistidas. Reexecutar a pasta inteira cria outro atendimento.

## Verificações complementares

Faça os cenários abaixo em novas ordens ou interrompa o runner na etapa apropriada. Eles não devem ser adicionados em sequência ao caminho de sucesso já entregue.

| Cenário | Como verificar | Resultado esperado |
| --- | --- | --- |
| Baixa de estoque | Durante a execução, envie `POST /estoque/{codigo}/baixas` com `{"quantidade":1}`; consulte o item pelo UUID | Quantidade reduzida pela baixa explícita; incluir peça no orçamento não equivale a consumi-la |
| Recusa de orçamento | Com orçamento enviado, registre decisão `RECUSADO` em `/ordens-servico/{id}/orcamento/notificacoes-aprovacao` ou use `/orcamento/recusa` | Orçamento recusado; tentativa de iniciar execução retorna `422` |
| Revisão de orçamento | De `AGUARDANDO_APROVACAO` ou `EM_EXECUCAO`, envie `PATCH /ordens-servico/{id}/status` com `{"status":"EM_DIAGNOSTICO"}` | OS volta para diagnóstico e orçamento reabre; ajuste itens, envie novamente e aprove antes de executar |
| Peça indisponível | Em orçamento aberto, inclua quantidade maior que a disponível no estoque | Evento interno `FaltaPecaEstoqueEvent`; a inclusão não é bloqueada automaticamente e não realiza reserva ou baixa |
| Entrega sem pagamento | Em OS `FINALIZADA` e ainda não paga, solicite `ENTREGUE` | `422`; registre pagamento antes de entregar |
| Diagnóstico fora de etapa | Tente registrar diagnóstico em OS que não está em `EM_DIAGNOSTICO` | `422` por regra de domínio |
| Token inválido | Consulte rota de negócio sem Bearer válido | Acesso recusado; a operação não deve ser executada |
| Tempo médio | Consulte `GET /ordens-servico/tempo-medio-execucao` | `200` com `tempoMedioExecucao`; não pressupõe duração fixa para a demonstração |

Na reabertura atual, os itens de serviço são removidos e os de peça são mantidos. Consulte a composição retornada, recadastre os serviços necessários e confira quantidades antes de reenviar o orçamento.

Os caminhos e corpos completos estão em [API.md](API.md). Falta de peça e notificações do domínio são tratadas por eventos internos e logs; não há envio integrado de e-mail/SMS nem cobrança por provedor de pagamentos.

## Conferir evidências

- Consulte a ordem por `GET /ordens-servico/{id}` e confira orçamento, pagamento, status e datas disponíveis.
- Confira o histórico de transições conforme o [modelo relacional](modelo-relacional/README.md#status-e-histórico-acrescentado-na-v4); use o documento para localizar a tabela, pois não há endpoint dedicado ao histórico.
- Na AWS, correlacione chamadas e transições com os logs e traces disponíveis no [guia de observabilidade](OBSERVABILIDADE.md). A execução da pasta não substitui os testes de coleta e alertas desse guia.
- Registre ambiente, horário, ID da OS e resultados HTTP para revisão. Remova Bearer tokens e credenciais das evidências compartilhadas.

## Diagnóstico de falhas

`400` pede conferir corpo e validação de entrada; `401/403`, token e caminho de autenticação; `422`, regras de negócio, códigos, vínculos, quantidades e status. `500`, timeout ou readiness indisponível pedem verificar logs e dependências. Consulte o [troubleshooting](TROUBLESHOOTING.md) e confirme a resposta real antes de avançar.
