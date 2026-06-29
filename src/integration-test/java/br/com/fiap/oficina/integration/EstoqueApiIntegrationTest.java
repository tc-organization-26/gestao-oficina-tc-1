package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class EstoqueApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarIncluirBaixarEExcluirItemEstoque() {
        var itemParaCadastrar = Map.of(
                "codigo", "OLEO-5W30",
                "descricao", "Oleo 5W30",
                "valorUnitario", 45.90,
                "quantidadeInicial", 10.000);

        var cadastro = postMap("/estoque", itemParaCadastrar);

        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals("OLEO-5W30", cadastro.getBody().get("codigo"));
        assertEquals(true, cadastro.getBody().get("ativo"));

        var itemId = cadastro.getBody().get("id").toString();

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
        assertEquals(1, listagem.getBody().size());
        assertEquals(itemId, ((Map<?, ?>) listagem.getBody().get(0)).get("id"));

        var exclusao = delete("/estoque/" + itemId);
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        var consultaDepoisDaExclusao = getMap("/estoque/" + itemId);
        assertEquals(false, consultaDepoisDaExclusao.getBody().get("ativo"));
    }

    @Test
    void deveConsultarItemPorCodigo() {
        var codigo = "FILTRO-AR-" + UUID.randomUUID().toString().substring(0, 8);
        var cadastro = postMap("/estoque", Map.of(
            "codigo", codigo, "descricao", "Filtro de ar",
                "valorUnitario", 35.0, "quantidadeInicial", 5.0));
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertEquals(codigo.toUpperCase(), cadastro.getBody().get("codigo"));

        var consulta = getMap("/estoque/codigo/" + cadastro.getBody().get("codigo"));
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertEquals(codigo.toUpperCase(), consulta.getBody().get("codigo"));
        assertEquals("Filtro de ar", consulta.getBody().get("descricao"));
    }

    @Test
    void deveRetornarErroAoConsultarCodigoInexistente() {
        var consulta = getMap("/estoque/codigo/INEXISTENTE-999");
        assertEquals(422, consulta.getStatusCode().value());
    }

    @Test
    void deveListarApenasItensAtivos() {
        var codigoAtivo = "ITEM-ATIVO-" + UUID.randomUUID().toString().substring(0, 8);
        var codigoInativo = "ITEM-INATIVO-" + UUID.randomUUID().toString().substring(0, 8);
        var itemAtivo = postMap("/estoque", Map.of(
            "codigo", codigoAtivo, "descricao", "Item ativo",
                "valorUnitario", 10.0, "quantidadeInicial", 5.0));
        var itemAtivoId = itemAtivo.getBody().get("id").toString();

        var itemInativo = postMap("/estoque", Map.of(
            "codigo", codigoInativo, "descricao", "Item inativo",
                "valorUnitario", 20.0, "quantidadeInicial", 3.0));
        var itemInativoId = itemInativo.getBody().get("id").toString();
        delete("/estoque/" + itemInativoId);

        var ativos = getList("/estoque/consulta/ativos");
        assertEquals(HttpStatus.OK, ativos.getStatusCode());
        assertEquals(1, ativos.getBody().size());
        assertEquals(itemAtivoId, ((Map<?, ?>) ativos.getBody().get(0)).get("id"));
    }
}


