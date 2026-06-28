package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class OrdemServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCriarEConsultarOrdemServico() {
        // given
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var ordemParaCriar = Map.of(
                "clienteId", clienteId,
                "veiculoId", veiculoId,
                "anotacoes", "Cliente relatou barulho ao frear.");

        // when
        var criacao = postMap("/ordens-servico", ordemParaCriar);

        // then
        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        assertNotNull(criacao.getBody());
        assertNotNull(criacao.getBody().get("id"));
        assertEquals(clienteId, criacao.getBody().get("clienteId"));
        assertEquals(veiculoId, criacao.getBody().get("veiculoId"));
        assertEquals("RECEBIDA", criacao.getBody().get("status"));
        assertEquals("Cliente relatou barulho ao frear.", criacao.getBody().get("anotacoes"));
        assertNotNull(criacao.getBody().get("dataRecebimento"));

        var ordemServicoId = criacao.getBody().get("id").toString();

        // when
        var consulta = getMap("/ordens-servico/" + ordemServicoId);

        // then
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(ordemServicoId, consulta.getBody().get("id"));
        assertEquals(clienteId, consulta.getBody().get("clienteId"));
        assertEquals(veiculoId, consulta.getBody().get("veiculoId"));
        assertEquals("RECEBIDA", consulta.getBody().get("status"));
    }

        @Test
        void deveAdicionarServicoAoOrcamentoDaOrdem() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();

        var ordemParaCriar = Map.of(
            "clienteId", clienteId,
            "veiculoId", veiculoId,
            "anotacoes", "Checklist inicial");

        var criacao = postMap("/ordens-servico", ordemParaCriar);
        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        var ordemServicoId = criacao.getBody().get("id").toString();

        var adicionarItem = postMap(
            "/ordens-servico/" + ordemServicoId + "/orcamento/servicos",
            Map.of("servicoId", servicoId, "quantidade", 1.0));

        assertEquals(HttpStatus.NO_CONTENT, adicionarItem.getStatusCode());
        }

        @Test
        void deveFecharOrcamentoEAtualizarStatusParaAguardandoAprovacao() {
        var clienteId = criarCliente();
        var veiculoId = criarVeiculo(clienteId);
        var servicoId = criarServico();

        var ordemParaCriar = Map.of(
            "clienteId", clienteId,
            "veiculoId", veiculoId,
            "anotacoes", "Diagnostico em andamento");

        var criacao = postMap("/ordens-servico", ordemParaCriar);
        assertEquals(HttpStatus.CREATED, criacao.getStatusCode());
        var ordemServicoId = criacao.getBody().get("id").toString();

        var iniciou = postMap("/ordens-servico/" + ordemServicoId + "/diagnostico/inicio", Map.of());
        assertEquals(HttpStatus.OK, iniciou.getStatusCode());
        assertEquals("EM_DIAGNOSTICO", iniciou.getBody().get("status"));

        var adicionouItem = postMap(
            "/ordens-servico/" + ordemServicoId + "/orcamento/servicos",
            Map.of("servicoId", servicoId, "quantidade", 2.0));
        assertEquals(HttpStatus.NO_CONTENT, adicionouItem.getStatusCode());

        var fecharOrcamento = postMap("/ordens-servico/" + ordemServicoId + "/orcamento/fechar", Map.of());
        assertEquals(HttpStatus.NO_CONTENT, fecharOrcamento.getStatusCode());

        var consulta = getMap("/ordens-servico/" + ordemServicoId);
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals("AGUARDANDO_APROVACAO", consulta.getBody().get("status"));
        }

    private String criarCliente() {
        var clienteParaCadastrar = Map.of(
                "nome", "Cliente Ordem",
                "cpfCnpj", "12345678901",
                "email", "ordem@email.com",
                "telefone", "11999999999");

        var cadastro = postMap("/clientes", clienteParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        return cadastro.getBody().get("id").toString();
    }

    private String criarVeiculo(String clienteId) {
        var veiculoParaCadastrar = Map.of(
                "clienteId", clienteId,
                "placa", "OSV1C23",
                "marca", "Fiat",
                "modelo", "Argo",
                "ano", 2022);

        var cadastro = postMap("/veiculos", veiculoParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        return cadastro.getBody().get("id").toString();
    }

    private String criarServico() {
        var servicoParaCadastrar = Map.of(
                "codigo", "DIAG-BASICO",
                "descricao", "Diagnostico basico",
                "valorUnitario", 120.0,
                "tempoEstimadoMinutos", 60);

        var cadastro = postMap("/servicos", servicoParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        return cadastro.getBody().get("id").toString();
    }
}