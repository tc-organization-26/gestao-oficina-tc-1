package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class VeiculoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirVeiculo() {
        // given
        var clienteId = criarCliente();
        var veiculoParaCadastrar = Map.of(
                "clienteId", clienteId,
                "placa", "ABC1D23",
                "marca", "Toyota",
                "modelo", "Corolla",
                "ano", 2020);

        // when
        var cadastro = postMap("/veiculos", veiculoParaCadastrar);

        // then
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals(clienteId, cadastro.getBody().get("clienteId"));
        assertEquals("ABC1D23", cadastro.getBody().get("placa"));

        var veiculoId = cadastro.getBody().get("id").toString();

        // when
        var consulta = getMap("/veiculos/" + veiculoId);

        // then
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(veiculoId, consulta.getBody().get("id"));
        assertEquals("Toyota", consulta.getBody().get("marca"));

        // given
        var veiculoParaAtualizar = Map.of(
                "modelo", "Civic",
                "marca", "Honda",
                "ano", 2021);

        // when
        put("/veiculos/" + veiculoId, veiculoParaAtualizar);
        var atualizado = getMap("/veiculos/" + veiculoId);

        // then
        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Honda", atualizado.getBody().get("marca"));
        assertEquals("Civic", atualizado.getBody().get("modelo"));

        // when
        var listagem = getList("/veiculos");

        // then
        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertEquals(1, listagem.getBody().size());
        assertEquals(veiculoId, ((Map<?, ?>) listagem.getBody().get(0)).get("id"));

        // when
        var exclusao = delete("/veiculos/" + veiculoId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        // when
        var consultaDepoisDaExclusao = getMap("/veiculos/" + veiculoId);

        // then
        assertEquals(422, consultaDepoisDaExclusao.getStatusCode().value());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertEquals("Veiculo não encontrado.", consultaDepoisDaExclusao.getBody().get("message"));
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

