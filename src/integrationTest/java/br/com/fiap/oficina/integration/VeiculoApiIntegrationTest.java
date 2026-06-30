package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VeiculoApiIntegrationTest extends AbstractApiIntegrationSupport {

    private final ArrayList<String> clienteIds = new ArrayList<>();
    private final ArrayList<String> veiculoIds = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        resetToken();
    }

    @BeforeEach
    void beforeEach() {
        resetToken();
        clienteIds.clear();
        veiculoIds.clear();
    }

    @AfterEach
    void afterEach() {
        limparVeiculos(veiculoIds);
        limparClientes(clienteIds);
    }

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirVeiculo() {
        var clienteId = criarCliente();
        var placa = placaUnica();
        var cadastro = postMap("/veiculos", Map.of(
                "clienteId", clienteId,
                "placa", placa,
                "marca", "Toyota",
                "modelo", "Corolla",
                "ano", 2020));

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals(clienteId, cadastro.getBody().get("clienteId"));
        assertEquals(placa, cadastro.getBody().get("placa"));

        var veiculoId = cadastro.getBody().get("id").toString();
        veiculoIds.add(veiculoId);

        var consulta = getMap("/veiculos/" + veiculoId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(veiculoId, consulta.getBody().get("id"));
        assertEquals("Toyota", consulta.getBody().get("marca"));

        put("/veiculos/" + veiculoId, Map.of("modelo", "Civic", "marca", "Honda", "ano", 2021));
        var atualizado = getMap("/veiculos/" + veiculoId);

        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Honda", atualizado.getBody().get("marca"));
        assertEquals("Civic", atualizado.getBody().get("modelo"));

        var listagem = getList("/veiculos");

        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertTrue(listagem.getBody().stream().anyMatch(item -> veiculoId.equals(((Map<?, ?>) item).get("id"))));

        var exclusao = delete("/veiculos/" + veiculoId);

        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());
        veiculoIds.remove(veiculoId);

        var consultaDepoisDaExclusao = getMap("/veiculos/" + veiculoId);

        assertEquals(422, consultaDepoisDaExclusao.getStatusCode().value());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertNotNull(consultaDepoisDaExclusao.getBody().get("message"));
    }

    private String criarCliente() {
        var resp = postMap("/clientes", Map.of(
                "nome", "Joao Silva",
                "cpfCnpj", documentoUnico(),
                "email", "joao-" + sufixo() + "@email.com",
                "telefone", "11999999999"));

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        var id = resp.getBody().get("id").toString();
        clienteIds.add(id);
        return id;
    }

    private static String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static String documentoUnico() {
        var numero = Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 100_000_000_000L);
        return "%011d".formatted(numero);
    }

    private static String placaUnica() {
        var numero = Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 1000);
        return "TST" + (numero / 100) + "A" + ((numero / 10) % 10) + (numero % 10);
    }
}


