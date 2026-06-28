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
}