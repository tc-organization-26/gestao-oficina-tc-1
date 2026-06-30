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
class ClienteApiIntegrationTest extends AbstractApiIntegrationSupport {

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
    void deveCadastrarConsultarAtualizarListarEExcluirCliente() {
        var cpfCnpj = documentoUnico();
        var clienteParaCadastrar = Map.of(
                "nome", "Maria Silva",
                "cpfCnpj", cpfCnpj,
                "email", "maria-" + sufixo() + "@email.com",
                "telefone", "11999999999");

        var cadastro = postMap("/clientes", clienteParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals("Maria Silva", cadastro.getBody().get("nome"));
        assertEquals(cpfCnpj, cadastro.getBody().get("cpfCnpj"));

        var clienteId = cadastro.getBody().get("id").toString();
        clienteIds.add(clienteId);

        var consulta = getMap("/clientes/" + clienteId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(clienteId, consulta.getBody().get("id"));
        assertEquals("Maria Silva", consulta.getBody().get("nome"));

        var clienteParaAtualizar = Map.of(
                "nome", "Maria Atualizada",
                "email", "maria.atualizada-" + sufixo() + "@email.com",
                "telefone", "11888888888");

        put("/clientes/" + clienteId, clienteParaAtualizar);
        var atualizado = getMap("/clientes/" + clienteId);

        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Maria Atualizada", atualizado.getBody().get("nome"));

        var listagem = getList("/clientes");

        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertTrue(listagem.getBody().stream().anyMatch(item -> clienteId.equals(((Map<?, ?>) item).get("id"))));

        var exclusao = delete("/clientes/" + clienteId);

        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());
        clienteIds.remove(clienteId);

        var consultaDepoisDaExclusao = getMap("/clientes/" + clienteId);

        assertEquals(422, consultaDepoisDaExclusao.getStatusCode().value());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertNotNull(consultaDepoisDaExclusao.getBody().get("message"));
    }

    @Test
    void deveListarVeiculosDoCliente() {
        var clienteId = criarCliente();

        var cadastro1 = postMap("/veiculos", Map.of(
                "clienteId", clienteId,
                "placa", placaUnica(),
                "marca", "Toyota",
                "modelo", "Corolla",
                "ano", 2020));
        var cadastro2 = postMap("/veiculos", Map.of(
                "clienteId", clienteId,
                "placa", placaUnica(),
                "marca", "Honda",
                "modelo", "Civic",
                "ano", 2021));

        assertEquals(HttpStatus.CREATED, cadastro1.getStatusCode());
        assertEquals(HttpStatus.CREATED, cadastro2.getStatusCode());
        veiculoIds.add(cadastro1.getBody().get("id").toString());
        veiculoIds.add(cadastro2.getBody().get("id").toString());

        var resposta = getList("/clientes/" + clienteId + "/veiculos");

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().stream().anyMatch(item -> veiculoIds.get(0).equals(((Map<?, ?>) item).get("id"))));
        assertTrue(resposta.getBody().stream().anyMatch(item -> veiculoIds.get(1).equals(((Map<?, ?>) item).get("id"))));
    }

    @Test
    void deveConsultarClientePorDocumento() {
        var cpfCnpj = documentoUnico();
        var cadastro = postMap("/clientes", Map.of(
                "nome", "Maria Documento",
                "cpfCnpj", cpfCnpj,
                "email", "maria.documento-" + sufixo() + "@email.com",
                "telefone", "11911112222"));

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        clienteIds.add(cadastro.getBody().get("id").toString());

        var consulta = getMap("/clientes/documento/" + cpfCnpj);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(cpfCnpj, consulta.getBody().get("cpfCnpj"));
        assertEquals("Maria Documento", consulta.getBody().get("nome"));
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


