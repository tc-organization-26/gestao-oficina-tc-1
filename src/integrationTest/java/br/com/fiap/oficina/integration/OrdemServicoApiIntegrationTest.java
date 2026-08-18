package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // CRIAÃ‡ÃƒO E CONSULTA
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

        var ordemId = criacao.getBody().get("id").toString();
        ordemIds.add(ordemId);
        var consulta = getMap("/ordens-servico/" + ordemId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals(ordemId, consulta.getBody().get("id"));
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

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // DIAGNÃ“STICO
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void deveIniciarERegistrarDiagnostico() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var ordemId = criarOrdem(clienteId, veiculoId);

        var inicio = postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        assertEquals(HttpStatus.OK, inicio.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", inicio.getBody().get("status"));

        var registro = postMap("/ordens-servico/" + ordemId + "/diagnostico",
                Map.of("descricao", "Motor com desgaste nas buchas"));
        assertEquals(HttpStatus.OK, registro.getStatusCode());
        assertEquals(ordemId, registro.getBody().get("id"));
        assertEquals("Motor com desgaste nas buchas", ((Map<?, ?>) registro.getBody().get("diagnostico")).get("descricao"));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // ORÃ‡AMENTO
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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
    void deveAdicionarPecaAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var itemEstoqueCodigo = criarItemEstoqueCodigo();
        var ordemId = criarOrdem(clienteId, veiculoId);
        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());

        var adicionarPeca = postMap("/ordens-servico/" + ordemId + "/orcamento/pecas",
                Map.of("codigo", itemEstoqueCodigo, "quantidade", 1.0));

        assertEquals(HttpStatus.OK, adicionarPeca.getStatusCode());
        assertEquals(ordemId, adicionarPeca.getBody().get("id"));
        assertNotNull(adicionarPeca.getBody().get("orcamento"));
    }

    @Test
    void deveFecharOrcamentoEAtualizarStatusParaAguardandoAprovacao() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 2.0));

        var fechar = postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());
        assertEquals(HttpStatus.OK, fechar.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", fechar.getBody().get("status"));
    }

    @Test
    void deveAprovarOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var aprovacao = postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", aprovacao.getBody().get("status"));
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
    void deveRecusarOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var recusa = postMap("/ordens-servico/" + ordemId + "/orcamento/recusa", Map.of());
        assertEquals(HttpStatus.OK, recusa.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", recusa.getBody().get("status"));
    }

    @Test
    void devePedirAjusteVoltandoParaDiagnostico() {
        var ordemId = prepararAteAguardandoAprovacao();

        var ajuste = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals(HttpStatus.OK, ajuste.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", ajuste.getBody().get("status"));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // EXECUÃ‡ÃƒO
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void deveIniciarExecucaoAposAprovacao() {
        var ordemId = prepararAteAguardandoAprovacao();
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());

        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
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

        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals(422, execucao.getStatusCode().value());
    }

    @Test
    void deveConsultarAcompanhamento() {
        var ordemId = prepararAteEmExecucao();

        var acompanhamento = getMap("/ordens-servico/" + ordemId + "/acompanhamento");
        assertEquals(HttpStatus.OK, acompanhamento.getStatusCode());
        assertEquals("EM_EXECUCAO", acompanhamento.getBody().get("status"));
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
    void devePedirAjusteDuranteExecucaoVoltandoParaDiagnostico() {
        var ordemId = prepararAteEmExecucao();

        var alteracao = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals(HttpStatus.OK, alteracao.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", alteracao.getBody().get("status"));
    }

    @Test
    void deveFinalizarExecucao() {
        var ordemId = prepararAteEmExecucao();

        var finalizacao = postMap("/ordens-servico/" + ordemId + "/execucao/finalizacao", Map.of());
        assertEquals(HttpStatus.OK, finalizacao.getStatusCode());
        assertEquals("FINALIZADA", finalizacao.getBody().get("status"));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // PAGAMENTO E ENTREGA
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

        var entrega = postMap("/ordens-servico/" + ordemId + "/entrega", Map.of());
        assertEquals(422, entrega.getStatusCode().value());
    }

    @Test
    void deveEntregarAposPagamento() {
        var ordemId = prepararAteFinalizada();
        postMap("/ordens-servico/" + ordemId + "/pagamento", Map.of());

        var entrega = postMap("/ordens-servico/" + ordemId + "/entrega", Map.of());
        assertEquals(HttpStatus.OK, entrega.getStatusCode());
        assertEquals("ENTREGUE", entrega.getBody().get("status"));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // FLUXO COMPLETO HAPPY PATH
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

        // 2. Iniciar diagnÃ³stico
        var inicio = postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        assertEquals("EM_DIAGNOSTICO", inicio.getBody().get("status"));

        // 3. Registrar diagnÃ³stico
        postMap("/ordens-servico/" + ordemId + "/diagnostico", Map.of("descricao", "Freios desgastados"));

        // 4. Adicionar serviÃ§o ao orÃ§amento
        var itemServico = postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        assertEquals(HttpStatus.OK, itemServico.getStatusCode());
        assertEquals(ordemId, itemServico.getBody().get("id"));

        // 5. Fechar orÃ§amento
        var fechamento = postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());
        assertEquals(HttpStatus.OK, fechamento.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", fechamento.getBody().get("status"));

        // 6. Aprovar orÃ§amento
        var aprovacao = postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());

        // 7. Iniciar execuÃ§Ã£o
        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));

        // 8. Acompanhamento (pode ser feito por atendente/gestor/cliente)
        var acompanhamento = getMap("/ordens-servico/" + ordemId + "/acompanhamento");
        assertEquals("EM_EXECUCAO", acompanhamento.getBody().get("status"));

        // 9. Baixar estoque durante execuÃ§Ã£o
        assertEquals(HttpStatus.OK,
                postMap("/estoque/" + itemEstoqueCodigo + "/baixas",
                        Map.of("quantidade", 1.0)).getStatusCode());

        // 10. Finalizar execuÃ§Ã£o
        var finalizacao = postMap("/ordens-servico/" + ordemId + "/execucao/finalizacao", Map.of());
        assertEquals("FINALIZADA", finalizacao.getBody().get("status"));

        // 11. Registrar pagamento
        var pagamento = postMap("/ordens-servico/" + ordemId + "/pagamento", Map.of());
        assertEquals("FINALIZADA", pagamento.getBody().get("status"));
        assertEquals(true, pagamento.getBody().get("pago"));

        // 12. Entregar
        var entrega = postMap("/ordens-servico/" + ordemId + "/entrega", Map.of());
        assertEquals("ENTREGUE", entrega.getBody().get("status"));
    }

    @Test
    void deveFluxoComAjusteDeOrcamento() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        // Iniciar diagnÃ³stico â†’ adicionar item â†’ fechar
        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());

        // Cliente pede ajuste â†’ volta EM_DIAGNOSTICO
        var ajuste = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals("EM_DIAGNOSTICO", ajuste.getBody().get("status"));

        // Re-adicionar serviÃ§o (orcamento reaberto) â†’ fechar novamente
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 2.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());

        // Aprovar â†’ iniciar execuÃ§Ã£o
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    @Test
    void deveFluxoComAlteracaoDeOrcamentoDuranteExecucao() {
        var servicoId = criarServico();
        var ordemId = prepararAteEmExecucao();

        // Alterar orÃ§amento durante execuÃ§Ã£o â†’ volta AGUARDANDO_APROVACAO
        var alteracao = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals("EM_DIAGNOSTICO", alteracao.getBody().get("status"));

        // Re-adicionar serviÃ§o â†’ fechar â†’ aprovar â†’ voltar a executar
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());

        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // HELPERS PRIVADOS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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

    private String criarItemEstoque() {
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var resp = postMap("/estoque", Map.of(
            "codigo", "FILTRO-" + suffix,
            "descricao", "Filtro de oleo " + suffix,
                "valorUnitario", 25.0, "quantidadeInicial", 10.0));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        var id = resp.getBody().get("id").toString();
        itemEstoqueIds.add(id);
        return id;
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

        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());

        return ordemId;
    }

    private String prepararAteEmExecucao() {
        var ordemId = prepararAteAguardandoAprovacao();
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        return ordemId;
    }

    private String prepararAteFinalizada() {
        var ordemId = prepararAteEmExecucao();
        postMap("/ordens-servico/" + ordemId + "/execucao/finalizacao", Map.of());
        return ordemId;
    }
}


