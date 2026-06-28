package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ClienteApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirCliente() {
        // given
        var clienteParaCadastrar = Map.of(
                "nome", "Maria Silva",
                "cpfCnpj", "12345678901",
                "email", "maria@email.com",
                "telefone", "11999999999");

        // when
        var cadastro = postMap("/clientes", clienteParaCadastrar);

        // then
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals("Maria Silva", cadastro.getBody().get("nome"));
        assertEquals("12345678901", cadastro.getBody().get("cpfCnpj"));

        var clienteId = cadastro.getBody().get("id").toString();

        // when
        var consulta = getMap("/clientes/" + clienteId);

        // then
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(clienteId, consulta.getBody().get("id"));
        assertEquals("Maria Silva", consulta.getBody().get("nome"));

        // given
        var clienteParaAtualizar = Map.of(
                "nome", "Maria Atualizada",
                "email", "maria.atualizada@email.com",
                "telefone", "11888888888");

        // when
        put("/clientes/" + clienteId, clienteParaAtualizar);
        var atualizado = getMap("/clientes/" + clienteId);

        // then
        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Maria Atualizada", atualizado.getBody().get("nome"));
        assertEquals("maria.atualizada@email.com", atualizado.getBody().get("email"));

        // when
        var listagem = getList("/clientes");

        // then
        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertEquals(1, listagem.getBody().size());
        assertEquals(clienteId, ((Map<?, ?>) listagem.getBody().get(0)).get("id"));

        // when
        var exclusao = delete("/clientes/" + clienteId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        // when
        var consultaDepoisDaExclusao = getMap("/clientes/" + clienteId);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, consultaDepoisDaExclusao.getStatusCode());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertEquals("Cliente não encontrado.", consultaDepoisDaExclusao.getBody().get("message"));
    }

    @Test
    void deveListarVeiculosDoCliente() {
        var clienteId = criarCliente();

        var veiculo1 = Map.of(
                "clienteId", clienteId,
                "placa", "ABC1D23",
                "marca", "Toyota",
                "modelo", "Corolla",
                "ano", 2020);

        var veiculo2 = Map.of(
                "clienteId", clienteId,
                "placa", "XYZ9W88",
                "marca", "Honda",
                "modelo", "Civic",
                "ano", 2021);

        var cadastro1 = postMap("/veiculos", veiculo1);
        var cadastro2 = postMap("/veiculos", veiculo2);

        assertEquals(HttpStatus.CREATED, cadastro1.getStatusCode());
        assertEquals(HttpStatus.CREATED, cadastro2.getStatusCode());

        var resposta = getList("/clientes/" + clienteId + "/veiculos");

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(2, resposta.getBody().size());

        var placas = resposta.getBody().stream()
                .map(item -> ((Map<?, ?>) item).get("placa").toString())
                .toList();

        assertEquals(true, placas.contains("ABC1D23"));
        assertEquals(true, placas.contains("XYZ9W88"));
    }

    private String criarCliente() {
        var clienteParaCadastrar = Map.of(
                "nome", "Joao Silva",
                "cpfCnpj", "12345678901",
                "email", "joao@email.com",
                "telefone", "11999999999");

        var cadastro = postMap("/clientes", clienteParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        return cadastro.getBody().get("id").toString();
    }
}

