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
class EstoqueApiIntegrationTest extends AbstractApiIntegrationSupport {

    private final ArrayList<String> itemEstoqueIds = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        resetToken();
    }

    @BeforeEach
    void beforeEach() {
        resetToken();
        itemEstoqueIds.clear();
    }

    @AfterEach
    void afterEach() {
        limparItensEstoque(itemEstoqueIds);
    }

    @Test
    void deveCadastrarConsultarAtualizarListarIncluirBaixarEExcluirItemEstoque() {
        var codigo = codigo("OLEO-5W30");
        var cadastro = postMap("/estoque", Map.of(
                "codigo", codigo,
                "descricao", "Oleo 5W30",
                "valorUnitario", 45.90,
                "quantidadeInicial", 10.000));

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals(codigo, cadastro.getBody().get("codigo"));
        assertEquals(true, cadastro.getBody().get("ativo"));

        var itemId = cadastro.getBody().get("id").toString();
        itemEstoqueIds.add(itemId);

        var consulta = getMap("/estoque/" + itemId);
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals("Oleo 5W30", consulta.getBody().get("descricao"));

        put("/estoque/" + itemId, Map.of("descricao", "Oleo 5W30 sintetico", "valorUnitario", 55.90));
        var atualizado = getMap("/estoque/" + itemId);
        assertEquals("Oleo 5W30 sintetico", atualizado.getBody().get("descricao"));

        var itemComInclusao = postMap("/estoque/" + itemId + "/inclusoes", Map.of("quantidade", 5.000));
        assertEquals(HttpStatus.OK, itemComInclusao.getStatusCode());
        assertEquals(15.0, ((Number) itemComInclusao.getBody().get("quantidadeDisponivel")).doubleValue());

        var itemComBaixa = postMap("/estoque/" + itemId + "/baixas", Map.of("quantidade", 3.000));
        assertEquals(HttpStatus.OK, itemComBaixa.getStatusCode());
        assertEquals(12.0, ((Number) itemComBaixa.getBody().get("quantidadeDisponivel")).doubleValue());

        var listagem = getList("/estoque");
        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertTrue(listagem.getBody().stream().anyMatch(item -> itemId.equals(((Map<?, ?>) item).get("id"))));

        var exclusao = delete("/estoque/" + itemId);
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        var consultaDepoisDaExclusao = getMap("/estoque/" + itemId);
        assertEquals(false, consultaDepoisDaExclusao.getBody().get("ativo"));
    }

    @Test
    void deveConsultarItemPorCodigo() {
        var codigo = codigo("FILTRO-AR");
        var cadastro = postMap("/estoque", Map.of(
                "codigo", codigo,
                "descricao", "Filtro de ar",
                "valorUnitario", 35.0,
                "quantidadeInicial", 5.0));
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        itemEstoqueIds.add(cadastro.getBody().get("id").toString());
        assertEquals(codigo, cadastro.getBody().get("codigo"));

        var consulta = getMap("/estoque/codigo/" + cadastro.getBody().get("codigo"));
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals(codigo, consulta.getBody().get("codigo"));
        assertEquals("Filtro de ar", consulta.getBody().get("descricao"));
    }

    @Test
    void deveRetornarErroAoConsultarCodigoInexistente() {
        var consulta = getMap("/estoque/codigo/INEXISTENTE-" + UUID.randomUUID());
        assertEquals(422, consulta.getStatusCode().value());
    }

    @Test
    void deveListarApenasItensAtivos() {
        var itemAtivo = postMap("/estoque", Map.of(
                "codigo", codigo("ITEM-ATIVO"),
                "descricao", "Item ativo",
                "valorUnitario", 10.0,
                "quantidadeInicial", 5.0));
        var itemAtivoId = itemAtivo.getBody().get("id").toString();
        itemEstoqueIds.add(itemAtivoId);

        var itemInativo = postMap("/estoque", Map.of(
                "codigo", codigo("ITEM-INATIVO"),
                "descricao", "Item inativo",
                "valorUnitario", 20.0,
                "quantidadeInicial", 3.0));
        var itemInativoId = itemInativo.getBody().get("id").toString();
        itemEstoqueIds.add(itemInativoId);
        delete("/estoque/" + itemInativoId);

        var ativos = getList("/estoque/consulta/ativos");
        assertEquals(HttpStatus.OK, ativos.getStatusCode());
        assertNotNull(ativos.getBody());
        assertTrue(ativos.getBody().stream().anyMatch(item -> itemAtivoId.equals(((Map<?, ?>) item).get("id"))));
        assertTrue(ativos.getBody().stream().noneMatch(item -> itemInativoId.equals(((Map<?, ?>) item).get("id"))));
    }

    private static String codigo(String prefixo) {
        return prefixo + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}