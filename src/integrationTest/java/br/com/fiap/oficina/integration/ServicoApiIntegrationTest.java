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
class ServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    private final ArrayList<String> servicoIds = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        resetToken();
    }

    @BeforeEach
    void beforeEach() {
        resetToken();
        servicoIds.clear();
    }

    @AfterEach
    void afterEach() {
        limparServicos(servicoIds);
    }

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirServico() {
        var codigo = "TROCA-OLEO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var cadastro = postMap("/servicos", Map.of(
                "codigo", codigo,
                "descricao", "Troca de oleo",
                "valorUnitario", 120.50,
                "tempoEstimadoMinutos", 60));

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals(codigo, cadastro.getBody().get("codigo"));

        var servicoId = cadastro.getBody().get("id").toString();
        servicoIds.add(servicoId);

        var consulta = getMap("/servicos/" + servicoId);

        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(servicoId, consulta.getBody().get("id"));
        assertEquals("Troca de oleo", consulta.getBody().get("descricao"));

        put("/servicos/" + servicoId, Map.of(
                "descricao", "Troca de oleo premium",
                "valorUnitario", 180.00,
                "tempoEstimadoMinutos", 90));
        var atualizado = getMap("/servicos/" + servicoId);

        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Troca de oleo premium", atualizado.getBody().get("descricao"));
        assertEquals(90, atualizado.getBody().get("tempoEstimadoMinutos"));

        var listagem = getList("/servicos");

        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertTrue(listagem.getBody().stream().anyMatch(item -> servicoId.equals(((Map<?, ?>) item).get("id"))));

        var exclusao = delete("/servicos/" + servicoId);

        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());
        servicoIds.remove(servicoId);

        var consultaDepoisDaExclusao = getMap("/servicos/" + servicoId);

        assertEquals(422, consultaDepoisDaExclusao.getStatusCode().value());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertNotNull(consultaDepoisDaExclusao.getBody().get("message"));
    }
}
