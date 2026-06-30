package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class OrdemServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    // ──────────────────────────────────────────────
    // CRIAÇÃO E CONSULTA
    // ──────────────────────────────────────────────

    @Test
    void deveCriarEConsultarOrdemServico() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);

        var criacao = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId, "veiculoId", veiculoId, "anotacoes", "Cliente relatou barulho ao frear."));

        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        assertNotNull(criacao.getBody().get("id"));
        assertEquals("RECEBIDA", criacao.getBody().get("status"));
        assertEquals(false, criacao.getBody().get("pago"));

        var ordemId = criacao.getBody().get("id").toString();
        var consulta = getMap("/ordens-servico/" + ordemId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals(ordemId, consulta.getBody().get("id"));
        assertEquals("RECEBIDA", consulta.getBody().get("status"));
    }

    // ──────────────────────────────────────────────
    // DIAGNÓSTICO
    // ──────────────────────────────────────────────

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
        assertEquals("EM_DIAGNOSTICO", registro.getBody().get("status"));
    }

    // ──────────────────────────────────────────────
    // ORÇAMENTO
    // ──────────────────────────────────────────────

    @Test
    void deveAdicionarServicoAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var ordemId = criarOrdem(clienteId, veiculoId);

        var adicionarItem = postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));

        assertEquals(HttpStatus.NO_CONTENT, adicionarItem.getStatusCode());
    }

    @Test
    void deveAdicionarPecaAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var itemEstoqueId = criarItemEstoque();
        var ordemId = criarOrdem(clienteId, veiculoId);
        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());

        var adicionarPeca = postMap("/ordens-servico/" + ordemId + "/orcamento/pecas",
                Map.of("itemEstoqueId", itemEstoqueId, "quantidade", 1.0));

        assertEquals(HttpStatus.NO_CONTENT, adicionarPeca.getStatusCode());
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
        assertEquals(HttpStatus.NO_CONTENT, fechar.getStatusCode());

        var consulta = getMap("/ordens-servico/" + ordemId);
        assertEquals("AGUARDANDO_APROVACAO", consulta.getBody().get("status"));
    }

    @Test
    void deveAprovarOrcamento() {
        var ordemId = prepararAteAguardandoAprovacao();

        var aprovacao = postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());
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

    // ──────────────────────────────────────────────
    // EXECUÇÃO
    // ──────────────────────────────────────────────

    @Test
    void deveIniciarExecucaoAposAprovacao() {
        var ordemId = prepararAteAguardandoAprovacao();
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());

        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals(HttpStatus.OK, execucao.getStatusCode());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
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
        assertEquals(2, historico.getBody().size());
        assertEquals(ordemId1, ((Map<?, ?>) historico.getBody().get(0)).get("id"));
        assertEquals(ordemId2, ((Map<?, ?>) historico.getBody().get(1)).get("id"));
    }

    @Test
    void deveListarOrdensPorStatusOrdenadasDaMaisAntigaParaMaisRecente() {
        var clienteId = criarCliente();
        var ordemId1 = criarOrdem(clienteId, criarVeiculo(clienteId));
        var ordemId2 = criarOrdem(clienteId, criarVeiculo(clienteId));

        var ordens = getList("/ordens-servico?status=RECEBIDA");

        assertEquals(HttpStatus.OK, ordens.getStatusCode());
        assertNotNull(ordens.getBody());
        assertEquals(true, ordens.getBody().size() >= 2);
        assertEquals(ordemId1, ((Map<?, ?>) ordens.getBody().get(0)).get("id"));
        assertEquals(ordemId2, ((Map<?, ?>) ordens.getBody().get(1)).get("id"));
    }

    @Test
    void deveBaixarEstoqueDuranteExecucao() {
        var itemEstoqueId = criarItemEstoque();
        var ordemId = prepararAteEmExecucao();

        var baixa = postMap("/estoque/" + itemEstoqueId + "/baixas",
                Map.of("quantidade", 2.0));
        assertEquals(HttpStatus.OK, baixa.getStatusCode());

        var itemAtualizado = getMap("/estoque/" + itemEstoqueId);
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

    // ──────────────────────────────────────────────
    // PAGAMENTO E ENTREGA
    // ──────────────────────────────────────────────

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

    // ──────────────────────────────────────────────
    // FLUXO COMPLETO HAPPY PATH
    // ──────────────────────────────────────────────

    @Test
    void deveExecutarFluxoCompletoHappyPath() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();
        var itemEstoqueId = criarItemEstoque();

        // 1. Criar ordem
        var criacao = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId, "veiculoId", veiculoId, "anotacoes", "Revisao completa"));
        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        assertEquals("RECEBIDA", criacao.getBody().get("status"));
        var ordemId = criacao.getBody().get("id").toString();

        // 2. Iniciar diagnóstico
        var inicio = postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        assertEquals("EM_DIAGNOSTICO", inicio.getBody().get("status"));

        // 3. Registrar diagnóstico
        postMap("/ordens-servico/" + ordemId + "/diagnostico", Map.of("descricao", "Freios desgastados"));

        // 4. Adicionar serviço ao orçamento
        assertEquals(HttpStatus.NO_CONTENT,
                postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                        Map.of("codigo", servicoId, "quantidade", 1.0)).getStatusCode());

        // 5. Fechar orçamento
        assertEquals(HttpStatus.NO_CONTENT,
                postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of()).getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", getMap("/ordens-servico/" + ordemId).getBody().get("status"));

        // 6. Aprovar orçamento
        var aprovacao = postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        assertEquals(HttpStatus.OK, aprovacao.getStatusCode());

        // 7. Iniciar execução
        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));

        // 8. Acompanhamento (pode ser feito por atendente/gestor/cliente)
        var acompanhamento = getMap("/ordens-servico/" + ordemId + "/acompanhamento");
        assertEquals("EM_EXECUCAO", acompanhamento.getBody().get("status"));

        // 9. Baixar estoque durante execução
        assertEquals(HttpStatus.OK,
                postMap("/estoque/" + itemEstoqueId + "/baixas",
                        Map.of("quantidade", 1.0)).getStatusCode());

        // 10. Finalizar execução
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

        // Iniciar diagnóstico → adicionar item → fechar
        postMap("/ordens-servico/" + ordemId + "/diagnostico/inicio", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());

        // Cliente pede ajuste → volta EM_DIAGNOSTICO
        var ajuste = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals("EM_DIAGNOSTICO", ajuste.getBody().get("status"));

        // Re-adicionar serviço (orcamento reaberto) → fechar novamente
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 2.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());

        // Aprovar → iniciar execução
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());
        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    @Test
    void deveFluxoComAlteracaoDeOrcamentoDuranteExecucao() {
        var servicoId = criarServico();
        var ordemId = prepararAteEmExecucao();

        // Alterar orçamento durante execução → volta AGUARDANDO_APROVACAO
        var alteracao = postMap("/ordens-servico/" + ordemId + "/orcamento/ajustes", Map.of());
        assertEquals("EM_DIAGNOSTICO", alteracao.getBody().get("status"));

        // Re-adicionar serviço → fechar → aprovar → voltar a executar
        postMap("/ordens-servico/" + ordemId + "/orcamento/servicos",
                Map.of("codigo", servicoId, "quantidade", 1.0));
        postMap("/ordens-servico/" + ordemId + "/orcamento/fechar", Map.of());
        postMap("/ordens-servico/" + ordemId + "/orcamento/aprovacao", Map.of());

        var execucao = postMap("/ordens-servico/" + ordemId + "/execucao/inicio", Map.of());
        assertEquals("EM_EXECUCAO", execucao.getBody().get("status"));
    }

    // ──────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ──────────────────────────────────────────────

    private String criarCliente() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var resp = postMap("/clientes", Map.of(
            "nome", "Cliente Ordem " + suffix,
            "cpfCnpj", "9" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 10),
            "email", "ordem-" + suffix + "@email.com",
            "telefone", "1199" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 7)));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        return resp.getBody().get("id").toString();
    }

    private String criarVeiculo(String clienteId) {
        var plate = "ABC" + (int) (Math.random() * 10) + "D" + (int) (Math.random() * 10) + (int) (Math.random() * 10);
        var resp = postMap("/veiculos", Map.of(
            "clienteId", clienteId, "placa", plate,
                "marca", "Fiat", "modelo", "Argo", "ano", 2022));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        return resp.getBody().get("id").toString();
    }

    private String criarServico() {
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var resp = postMap("/servicos", Map.of(
            "codigo", "DIAG-" + suffix,
            "descricao", "Diagnostico basico " + suffix,
                "valorUnitario", 120.0, "tempoEstimadoMinutos", 60));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        return resp.getBody().get("codigo").toString();
    }

    private String criarItemEstoque() {
        var suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var resp = postMap("/estoque", Map.of(
            "codigo", "FILTRO-" + suffix,
            "descricao", "Filtro de oleo " + suffix,
                "valorUnitario", 25.0, "quantidadeInicial", 10.0));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        return resp.getBody().get("id").toString();
    }

    private String criarOrdem(String clienteId, String veiculoId) {
        var resp = postMap("/ordens-servico", Map.of(
                "clienteId", clienteId, "veiculoId", veiculoId, "anotacoes", "Revisao"));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        return resp.getBody().get("id").toString();
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
