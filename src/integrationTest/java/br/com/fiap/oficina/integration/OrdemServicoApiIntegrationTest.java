package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrdemServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @org.springframework.beans.factory.annotation.Autowired
    private br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway ordemServicoGateway;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    @Test
    void devePublicarTransicaoSomenteAposCommitENaoAposRollback() {
        var clienteId = criarCliente();
        var ordemId = criarOrdem(clienteId, criarVeiculo(clienteId));
        var id = new br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId(UUID.fromString(ordemId));
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
                br.com.fiap.oficina.ordemservico.interfaceadapters.observability.TransicaoStatusLogPublisher.class);
        var logs = new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        logs.start();
        logger.addAppender(logs);
        var transactions = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        try {
            transactions.executeWithoutResult(tx -> {
                var ordem = ordemServicoGateway.buscarPorId(id).orElseThrow();
                ordem.iniciarDiagnostico();
                ordemServicoGateway.salvar(ordem);
                assertTrue(logs.list.isEmpty());
                tx.setRollbackOnly();
            });
            assertTrue(logs.list.isEmpty());
            assertEquals(1, jdbcTemplate.queryForObject("select count(*) from ordem_servico_status_historico where ordem_id = ?", Integer.class, id.value()));

            transactions.executeWithoutResult(tx -> {
                var ordem = ordemServicoGateway.buscarPorId(id).orElseThrow();
                ordem.iniciarDiagnostico();
                ordemServicoGateway.salvar(ordem);
                assertTrue(logs.list.isEmpty());
            });
            assertEquals(2, logs.list.size()); // status_changed and status_duration, after commit only
            assertEquals(2, jdbcTemplate.queryForObject("select count(*) from ordem_servico_status_historico where ordem_id = ?", Integer.class, id.value()));
        } finally { logger.detachAppender(logs); logs.stop(); }
    }

    private final ArrayList<String> clienteIds = new ArrayList<>();
    private final ArrayList<String> veiculoIds = new ArrayList<>();
    private final ArrayList<String> servicoIds = new ArrayList<>();
    private final ArrayList<String> itemEstoqueIds = new ArrayList<>();
    private final ArrayList<String> ordemIds = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        resetToken();
    }

    @BeforeEach
    void beforeEach() {
        resetToken();
        clienteIds.clear();
        veiculoIds.clear();
        servicoIds.clear();
        itemEstoqueIds.clear();
        ordemIds.clear();
    }

    @AfterEach
    void afterEach() {
        limparOrdens(ordemIds);
        limparVeiculos(veiculoIds);
        limparClientes(clienteIds);
        limparServicos(servicoIds);
        limparItensEstoque(itemEstoqueIds);
    }

    // ----------------------------------------------
    // CRIAÇÃO E CONSULTA
    // ----------------------------------------------

    @Test
    void devePersistirDuracaoEntreStatusSemDuplicarAtualizacaoIdempotente() {
        var clienteId = criarCliente();
        var ordemId = criarOrdem(clienteId, criarVeiculo(clienteId));
        var id = UUID.fromString(ordemId);
        jdbcTemplate.update("update ordem_servico_status_historico set entrou_em = clock_timestamp() - interval '2 minutes' where ordem_id = ?", id);

        var alteracao = patchMap("/ordens-servico/" + ordemId + "/status", Map.of("status", "EM_DIAGNOSTICO"));
        assertEquals(HttpStatus.OK, alteracao.getStatusCode());
        assertNotNull(alteracao.getHeaders().getFirst("X-Request-ID"));
        var historico = jdbcTemplate.queryForMap("select * from ordem_servico_status_historico where ordem_id = ? order by id desc limit 1", id);
        assertEquals(0, ((Number) historico.get("status_anterior")).intValue());
        assertEquals(1, ((Number) historico.get("status_atual")).intValue());
        assertTrue(((Number) historico.get("duracao_anterior_ms")).longValue() >= 120000);

        assertEquals(HttpStatus.OK, patchMap("/ordens-servico/" + ordemId + "/status", Map.of("status", "EM_DIAGNOSTICO")).getStatusCode());
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from ordem_servico_status_historico where ordem_id = ?", Integer.class, id));
    }

    @Test
    void deveReverterHistoricoJuntoComTransacao() {
        var clienteId = criarCliente();
        var ordemId = criarOrdem(clienteId, criarVeiculo(clienteId));
        var id = UUID.fromString(ordemId);
        var manager = new org.springframework.jdbc.datasource.DataSourceTransactionManager(jdbcTemplate.getDataSource());
        new org.springframework.transaction.support.TransactionTemplate(manager).executeWithoutResult(tx -> {
            jdbcTemplate.update("update ordem_servico set status_ordem_servico = 1 where id = ?", id);
            assertEquals(2, jdbcTemplate.queryForObject("select count(*) from ordem_servico_status_historico where ordem_id = ?", Integer.class, id));
            tx.setRollbackOnly();
        });
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from ordem_servico_status_historico where ordem_id = ?", Integer.class, id));
    }

    @Test
    void deveCriarEConsultarOrdemServico() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);

        var criacao = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId,
                "veiculoId", veiculoId,
                "servicos", List.of(),
                "pecas", List.of(),
                "anotacoes", "Cliente relatou barulho ao frear."));

        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        assertNotNull(criacao.getBody().get("id"));
        assertNotNull(criacao.getBody().get("numero"));
        var numeroCriacao = ((Number) criacao.getBody().get("numero")).longValue();
        assertTrue(numeroCriacao > 0);

        var ordemId = criacao.getBody().get("id").toString();
        ordemIds.add(ordemId);
        var consulta = getMap("/ordens-servico/" + ordemId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals(ordemId, consulta.getBody().get("id"));
        assertEquals(numeroCriacao, ((Number) consulta.getBody().get("numero")).longValue());
        assertEquals("RECEBIDA", consulta.getBody().get("status"));
    }

    @Test
    void deveConsultarStatusDaOrdemServico() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var ordemId = criarOrdem(clienteId, veiculoId);

        var status = getMap("/ordens-servico/" + ordemId + "/status");

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(ordemId, status.getBody().get("id"));
        assertEquals("RECEBIDA", status.getBody().get("status"));
        assertEquals("Recebida", status.getBody().get("descricao"));
    }

    @Test
    void deveCriarOrdemServicoComServicosEPecasNoBody() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoCodigo = criarServico();
        var itemEstoqueCodigo = criarItemEstoqueCodigo();

        var criacao = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId,
                "veiculoId", veiculoId,
                "servicos", List.of(Map.of("codigo", servicoCodigo, "quantidade", 1.0)),
                "pecas", List.of(Map.of("codigo", itemEstoqueCodigo, "quantidade", 2.0)),
                "anotacoes", "Revisao com itens iniciais"));

        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        var ordemId = criacao.getBody().get("id").toString();
        ordemIds.add(ordemId);

        var consulta = getMap("/ordens-servico/" + ordemId);
        var orcamento = (Map<?, ?>) consulta.getBody().get("orcamento");
        assertEquals(1, ((List<?>) orcamento.get("itensServico")).size());
        assertEquals(1, ((List<?>) orcamento.get("itensPeca")).size());
    }

    // ----------------------------------------------
    // DIAGNÓSTICO
    // ----------------------------------------------

    @Test
    void deveIniciarERegistrarDiagnostico() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var ordemId = criarOrdem(clienteId, veiculoId);

        var inicio = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals(HttpStatus.OK, inicio.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", inicio.getBody().get("status"));

        var registro = postMap("/ordens-servico/" + ordemId + "/diagnostico",
                Map.of("descricao", "Motor com desgaste nas buchas"));
        assertEquals(HttpStatus.OK, registro.getStatusCode());
        assertEquals(ordemId, registro.getBody().get("id"));
        assertEquals("Motor com desgaste nas buchas", ((Map<?, ?>) registro.getBody().get("diagnostico")).get("descricao"));
    }

    // ----------------------------------------------
    // ORÇAMENTO
    // ----------------------------------------------

    @Test
    void deveAdicionarServicoAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        var adicionarItem = postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));

        assertEquals(HttpStatus.OK, adicionarItem.getStatusCode());
        assertEquals(ordemId, adicionarItem.getBody().get("id"));
        assertNotNull(adicionarItem.getBody().get("orcamento"));
    }

    @Test
    void deveSomarQuantidadeAoAdicionarMesmoServicoAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        var adicionarItemRepetido = postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));

        var orcamento = (Map<?, ?>) adicionarItemRepetido.getBody().get("orcamento");
        var itensServico = (List<?>) orcamento.get("itensServico");
        var item = (Map<?, ?>) itensServico.get(0);
        assertEquals(HttpStatus.OK, adicionarItemRepetido.getStatusCode());
        assertEquals(1, itensServico.size());
        assertEquals(2.0, ((Number) item.get("quantidade")).doubleValue());
    }

    @Test
    void deveAdicionarPecaAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var itemEstoqueCodigo = criarItemEstoqueCodigo();
        var ordemId = criarOrdem(clienteId, veiculoId);
        alterarStatus(ordemId, "EM_DIAGNOSTICO");

        var adicionarPeca = postMap("/ordens-servico/" + ordemId + "/orcamento/pecas",
                Map.of("codigo", itemEstoqueCodigo, "quantidade", 1.0));

        assertEquals(HttpStatus.OK, adicionarPeca.getStatusCode());
        assertEquals(ordemId, adicionarPeca.getBody().get("id"));
        assertNotNull(adicionarPeca.getBody().get("orcamento"));
    }

    @Test
    void deveSomarQuantidadeAoAdicionarMesmaPecaAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var itemEstoqueCodigo = criarItemEstoqueCodigo();
        var ordemId = criarOrdem(clienteId, veiculoId);
        alterarStatus(ordemId, "EM_DIAGNOSTICO");

        postMap("/ordens-servico/" + ordemId + "/orcamento/pecas",
                Map.of("codigo", itemEstoqueCodigo, "quantidade", 1.0));
        var adicionarPecaRepetida = postMap("/ordens-servico/" + ordemId + "/orcamento/pecas",
                Map.of("codigo", itemEstoqueCodigo, "quantidade", 1.0));

        var orcamento = (Map<?, ?>) adicionarPecaRepetida.getBody().get("orcamento");
        var itensPeca = (List<?>) orcamento.get("itensPeca");
        var item = (Map<?, ?>) itensPeca.get(0);
        assertEquals(HttpStatus.OK, adicionarPecaRepetida.getStatusCode());
        assertEquals(1, itensPeca.size());
        assertEquals(2.0, ((Number) item.get("quantidade")).doubleValue());
    }

    @Test
    void deveFecharOrcamentoEAtualizarStatusParaAguardandoAprovacao() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        alterarStatus(ordemId, "EM_DIAGNOSTICO");
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 2.0));

        var fechar = alterarStatus(ordemId, "AGUARDANDO_APROVACAO");
        assertEquals(HttpStatus.OK, fechar.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", fechar.getBody().get("status"));
    }

    @Test
    void deveReceberNotificacaoExternaDeAprovacaoDoOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var aprovacao = postMap("/ordens-servico/" + ordemId + "/orcamento/notificacoes-aprovacao",
                Map.of(
                        "decisao", "APROVADO",
                        "origem", "whatsapp",
                        "protocoloExterno", UUID.randomUUID().toString()));

        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());
        assertEquals(ordemId, aprovacao.getBody().get("id"));
        assertEquals("AGUARDANDO_APROVACAO", aprovacao.getBody().get("status"));
    }

    @Test
    void deveReceberNotificacaoExternaDeRecusaDoOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var recusa = postMap("/ordens-servico/" + ordemId + "/orcamento/notificacoes-aprovacao",
                Map.of(
                        "decisao", "RECUSADO",
                        "origem", "portal-cliente",
                        "protocoloExterno", UUID.randomUUID().toString()));

        assertEquals(HttpStatus.OK, recusa.getStatusCode());
        assertEquals(ordemId, recusa.getBody().get("id"));
        assertEquals("AGUARDANDO_APROVACAO", recusa.getBody().get("status"));
    }

    @Test
    void deveRecusarOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var recusa = postMap("/ordens-servico/" + ordemId + "/orcamento/recusa", Map.of());
        assertEquals(HttpStatus.OK, recusa.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", recusa.getBody().get("status"));
    }

    @Test
    void deveVoltarParaDiagnosticoQuandoSolicitadoAjusteDeOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var ajuste = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals(HttpStatus.OK, ajuste.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", ajuste.getBody().get("status"));
    }

    // ----------------------------------------------
    // EXECUÇÃO
    // ----------------------------------------------

    @Test
    void deveIniciarExecucaoAposAprovacao() {
        var ordemId = prepararAteAguardandoAprovacao();
        aprovarOrcamentoPorNotificacao(ordemId);

        var execucao = alterarStatus(ordemId, "EM_EXECUCAO");
        assertEquals(HttpStatus.OK, execucao.getStatusCode());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    @Test
    void deveAtualizarStatusViaPatch() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var ordemId = criarOrdem(clienteId, veiculoId);

        var status = patchMap("/ordens-servico/" + ordemId + "/status",
                Map.of("status", "EM_DIAGNOSTICO"));

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(ordemId, status.getBody().get("id"));
        assertEquals("EM_DIAGNOSTICO", status.getBody().get("status"));
    }

    @Test
    void naoDeveIniciarExecucaoSemOrcamentoAprovado() {
        var ordemId = prepararAteAguardandoAprovacao();

        var execucao = alterarStatus(ordemId, "EM_EXECUCAO");
        assertEquals(422, execucao.getStatusCode().value());
    }

    @Test
    void deveConsultarStatusDaOrdem() {
        var ordemId = prepararAteEmExecucao();

        var status = getMap("/ordens-servico/" + ordemId + "/status");
        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals("EM_EXECUCAO", status.getBody().get("status"));
    }

    @Test
    void deveListarOrdensDeServicoPorCliente() {
        var clienteId = criarCliente();
        var veiculoId1 = criarVeiculo(clienteId);
        var veiculoId2 = criarVeiculo(clienteId);

        var ordemId1 = criarOrdem(clienteId, veiculoId1);
        var ordemId2 = criarOrdem(clienteId, veiculoId2);

        var historico = getList("/ordens-servico?clienteId=" + clienteId);

        assertEquals(HttpStatus.OK, historico.getStatusCode());
        assertNotNull(historico.getBody());
        var ids = historico.getBody().stream()
                .map(item -> ((Map<?, ?>) item).get("id"))
                .toList();
        assertEquals(2, ids.size());
        assertEquals(ordemId1, ids.get(0));
        assertEquals(ordemId2, ids.get(1));
    }

    @Test
    void deveListarTodasAsOrdensDeServicoSemFiltro() {
        var clienteId = criarCliente();
        var ordemId1 = criarOrdem(clienteId, criarVeiculo(clienteId));
        var ordemId2 = criarOrdem(clienteId, criarVeiculo(clienteId));

        var ordens = getList("/ordens-servico");

        assertEquals(HttpStatus.OK, ordens.getStatusCode());
        assertNotNull(ordens.getBody());
        var ids = ordens.getBody().stream()
                .map(item -> ((Map<?, ?>) item).get("id"))
                .toList();
        assertEquals(true, ids.contains(ordemId1));
        assertEquals(true, ids.contains(ordemId2));
    }

    @Test
    void deveListarOrdensPorStatusOrdenadasDaMaisAntigaParaMaisRecente() {
        var clienteId = criarCliente();
        var ordemId1 = criarOrdem(clienteId, criarVeiculo(clienteId));
        var ordemId2 = criarOrdem(clienteId, criarVeiculo(clienteId));

        var ordens = getList("/ordens-servico?status=RECEBIDA");

        assertEquals(HttpStatus.OK, ordens.getStatusCode());
        assertNotNull(ordens.getBody());
        var ids = ordens.getBody().stream()
                .map(item -> ((Map<?, ?>) item).get("id"))
                .toList();
        assertEquals(true, ids.contains(ordemId1));
        assertEquals(true, ids.contains(ordemId2));
        assertEquals(true, ids.indexOf(ordemId1) < ids.indexOf(ordemId2));
    }

    @Test
    void deveListarApenasOrdensAtivasOrdenadasPorPrioridadeEAntiguidade() {
        var clienteId = criarCliente();
        var ordemRecebida = criarOrdem(clienteId, criarVeiculo(clienteId));
        var ordemExecucao = prepararAteEmExecucao();
        var ordemFinalizada = prepararAteFinalizada();

        var ordens = getList("/ordens-servico");

        assertEquals(HttpStatus.OK, ordens.getStatusCode());
        assertNotNull(ordens.getBody());
        var ids = ordens.getBody().stream()
                .map(item -> ((Map<?, ?>) item).get("id"))
                .toList();
        assertEquals(true, ids.contains(ordemRecebida));
        assertEquals(true, ids.contains(ordemExecucao));
        assertEquals(false, ids.contains(ordemFinalizada));
        assertEquals(true, ids.indexOf(ordemExecucao) < ids.indexOf(ordemRecebida));
    }

    @Test
    void deveConsultarTempoMedioExecucao() {
        var ordemId = prepararAteFinalizada();

        var tempoMedio = getMap("/ordens-servico/tempo-medio-execucao");

        assertEquals(HttpStatus.OK, tempoMedio.getStatusCode());
        assertNotNull(tempoMedio.getBody());
        assertEquals(true, tempoMedio.getBody().containsKey("tempoMedioExecucao"));
        assertNotNull(tempoMedio.getBody().get("tempoMedioExecucao"));
        assertEquals("FINALIZADA", getMap("/ordens-servico/" + ordemId).getBody().get("status"));
    }

    @Test
    void deveBaixarEstoqueDuranteExecucao() {
        var itemEstoqueCodigo = criarItemEstoqueCodigo();
        var ordemId = prepararAteEmExecucao();

        var baixa = postMap("/estoque/" + itemEstoqueCodigo + "/baixas",
                Map.of("quantidade", 2.0));
        assertEquals(HttpStatus.OK, baixa.getStatusCode());

        var itemAtualizado = getMap("/estoque/codigo/" + itemEstoqueCodigo);
        assertEquals(8.0, ((Number) itemAtualizado.getBody().get("quantidadeDisponivel")).doubleValue());
    }

    @Test
    void deveVoltarParaDiagnosticoDuranteExecucaoQuandoSolicitadoAjuste() {
        var ordemId = prepararAteEmExecucao();

        var alteracao = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals(HttpStatus.OK, alteracao.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", alteracao.getBody().get("status"));
    }

    @Test
    void deveFinalizarExecucao() {
        var ordemId = prepararAteEmExecucao();

        var finalizacao = alterarStatus(ordemId, "FINALIZADA");
        assertEquals(HttpStatus.OK, finalizacao.getStatusCode());
        assertEquals("FINALIZADA", finalizacao.getBody().get("status"));
    }

    // ----------------------------------------------
    // PAGAMENTO E ENTREGA
    // ----------------------------------------------

    @Test
    void deveRegistrarPagamento() {
        var ordemId = prepararAteFinalizada();

        var pagamento = postMap("/ordens-servico/" + ordemId + "/pagamento", Map.of());
        assertEquals(HttpStatus.OK, pagamento.getStatusCode());
        assertEquals("FINALIZADA", pagamento.getBody().get("status"));
        assertEquals(true, pagamento.getBody().get("pago"));
    }

    @Test
    void naoDeveEntregarSemPagamento() {
        var ordemId = prepararAteFinalizada();

        var entrega = alterarStatus(ordemId, "ENTREGUE");
        assertEquals(422, entrega.getStatusCode().value());
    }

    @Test
    void deveEntregarAposPagamento() {
        var ordemId = prepararAteFinalizada();
        postMap("/ordens-servico/" + ordemId + "/pagamento", Map.of());

        var entrega = alterarStatus(ordemId, "ENTREGUE");
        assertEquals(HttpStatus.OK, entrega.getStatusCode());
        assertEquals("ENTREGUE", entrega.getBody().get("status"));
    }

    // ----------------------------------------------
    // FLUXO COMPLETO HAPPY PATH
    // ----------------------------------------------

    @Test
    void deveExecutarFluxoCompletoHappyPath() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var itemEstoqueCodigo = criarItemEstoqueCodigo();

        // 1. Criar ordem
        var criacao = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId,
                "veiculoId", veiculoId,
                "servicos", List.of(),
                "pecas", List.of(),
                "anotacoes", "Revisao completa"));
        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        var ordemId = criacao.getBody().get("id").toString();
        ordemIds.add(ordemId);

        // 2. Iniciar diagnóstico
        var inicio = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals("EM_DIAGNOSTICO", inicio.getBody().get("status"));

        // 3. Registrar diagnóstico
        postMap("/ordens-servico/" + ordemId + "/diagnostico", Map.of("descricao", "Freios desgastados"));

        // 4. Adicionar serviço ao orçamento
        var itemServico = postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        assertEquals(HttpStatus.OK, itemServico.getStatusCode());
        assertEquals(ordemId, itemServico.getBody().get("id"));

        // 5. Fechar orçamento
        var fechamento = alterarStatus(ordemId, "AGUARDANDO_APROVACAO");
        assertEquals(HttpStatus.OK, fechamento.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", fechamento.getBody().get("status"));

        // 6. Aprovar orçamento
        var aprovacao = aprovarOrcamentoPorNotificacao(ordemId);
        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());

        // 7. Iniciar execução
        var execucao = alterarStatus(ordemId, "EM_EXECUCAO");
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));

        // 8. Consulta de status (pode ser feita por atendente/gestor/cliente)
        var status = getMap("/ordens-servico/" + ordemId + "/status");
        assertEquals("EM_EXECUCAO", status.getBody().get("status"));

        // 9. Baixar estoque durante execução
        assertEquals(HttpStatus.OK,
                postMap("/estoque/" + itemEstoqueCodigo + "/baixas",
                        Map.of("quantidade", 1.0)).getStatusCode());

        // 10. Finalizar execução
        var finalizacao = alterarStatus(ordemId, "FINALIZADA");
        assertEquals("FINALIZADA", finalizacao.getBody().get("status"));

        // 11. Registrar pagamento
        var pagamento = postMap("/ordens-servico/" + ordemId + "/pagamento", Map.of());
        assertEquals("FINALIZADA", pagamento.getBody().get("status"));
        assertEquals(true, pagamento.getBody().get("pago"));

        // 12. Entregar
        var entrega = alterarStatus(ordemId, "ENTREGUE");
        assertEquals("ENTREGUE", entrega.getBody().get("status"));
    }

    @Test
    void deveFluxoComAjusteDeOrcamento() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        // Iniciar diagnóstico -> adicionar item -> fechar
        alterarStatus(ordemId, "EM_DIAGNOSTICO");
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        alterarStatus(ordemId, "AGUARDANDO_APROVACAO");

        // Cliente pede ajuste -> volta EM_DIAGNOSTICO
        var ajuste = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals("EM_DIAGNOSTICO", ajuste.getBody().get("status"));

        // Re-adicionar serviço (orçamento reaberto) -> fechar novamente
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 2.0));
        alterarStatus(ordemId, "AGUARDANDO_APROVACAO");

        // Aprovar -> iniciar execução
        aprovarOrcamentoPorNotificacao(ordemId);
        var execucao = alterarStatus(ordemId, "EM_EXECUCAO");
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    @Test
    void deveFluxoComAlteracaoDeOrcamentoDuranteExecucao() {
        var servicoId = criarServico();
        var ordemId = prepararAteEmExecucao();

        // Alterar orçamento durante execução -> volta EM_DIAGNOSTICO
        var alteracao = alterarStatus(ordemId, "EM_DIAGNOSTICO");
        assertEquals("EM_DIAGNOSTICO", alteracao.getBody().get("status"));

        // Re-adicionar serviço -> fechar -> aprovar -> voltar a executar
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        alterarStatus(ordemId, "AGUARDANDO_APROVACAO");
        aprovarOrcamentoPorNotificacao(ordemId);

        var execucao = alterarStatus(ordemId, "EM_EXECUCAO");
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    // ----------------------------------------------
    // HELPERS PRIVADOS
    // ----------------------------------------------

    private String criarCliente() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var resp = postMap("/clientes", Map.of(
            "nome", "Cliente Ordem " + suffix,
            "cpfCnpj", documentoUnico(),
            "email", "ordem-" + suffix + "@email.com",
            "telefone", "11999999999"));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        var id = resp.getBody().get("id").toString();
        clienteIds.add(id);
        return id;
    }

    private String criarVeiculo(String clienteId) {
        var plate = placaUnica();
        var resp = postMap("/veiculos", Map.of(
            "clienteId", clienteId, "placa", plate,
                "marca", "Fiat", "modelo", "Argo", "ano", 2022));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        var id = resp.getBody().get("id").toString();
        veiculoIds.add(id);
        return id;
    }

    private String criarServico() {
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var resp = postMap("/servicos", Map.of(
            "codigo", "DIAG-" + suffix,
            "descricao", "Diagnostico basico " + suffix,
                "valorUnitario", 120.0, "tempoEstimadoMinutos", 60));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        servicoIds.add(resp.getBody().get("id").toString());
        return resp.getBody().get("codigo").toString();
    }

    private String criarItemEstoqueCodigo() {
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var resp = postMap("/estoque", Map.of(
            "codigo", "FILTRO-" + suffix,
            "descricao", "Filtro de oleo " + suffix,
                "valorUnitario", 25.0, "quantidadeInicial", 10.0));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        itemEstoqueIds.add(resp.getBody().get("id").toString());
        return resp.getBody().get("codigo").toString();
    }

    private String criarOrdem(String clienteId, String veiculoId) {
        var resp = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId,
                "veiculoId", veiculoId,
                "servicos", List.of(),
                "pecas", List.of(),
                "anotacoes", "Revisao"));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        var id = resp.getBody().get("id").toString();
        ordemIds.add(id);
        return id;
    }

    private static String documentoUnico() {
        var numero = Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 100_000_000_000L);
        return "%011d".formatted(numero);
    }

    private static String placaUnica() {
        var numero = Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 1000);
        return "TST" + (numero / 100) + "A" + ((numero / 10) % 10) + (numero % 10);
    }

    private String prepararAteAguardandoAprovacao() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        alterarStatus(ordemId, "EM_DIAGNOSTICO");
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        alterarStatus(ordemId, "AGUARDANDO_APROVACAO");

        return ordemId;
    }

    private String prepararAteEmExecucao() {
        var ordemId = prepararAteAguardandoAprovacao();
        aprovarOrcamentoPorNotificacao(ordemId);
        alterarStatus(ordemId, "EM_EXECUCAO");
        return ordemId;
    }

    private org.springframework.http.ResponseEntity<Map> aprovarOrcamentoPorNotificacao(String ordemId) {
        return postMap("/ordens-servico/" + ordemId + "/orcamento/notificacoes-aprovacao",
                Map.of(
                        "decisao", "APROVADO",
                        "origem", "whatsapp",
                        "protocoloExterno", UUID.randomUUID().toString()));
    }

    private String prepararAteFinalizada() {
        var ordemId = prepararAteEmExecucao();
        alterarStatus(ordemId, "FINALIZADA");
        return ordemId;
    }

    private org.springframework.http.ResponseEntity<Map> alterarStatus(String ordemId, String status) {
        return patchMap("/ordens-servico/" + ordemId + "/status", Map.of("status", status));
    }
}
