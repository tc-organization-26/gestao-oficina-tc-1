package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class EstoqueApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarIncluirBaixarEExcluirItemEstoque() {
        // given
        var itemParaCadastrar = Map.of(
                "codigo", "OLEO-5W30",
                "descricao", "Oleo 5W30",
                "valorUnitario", 45.90,
                "quantidadeInicial", 10.000);

        // when
        var cadastro = postMap("/estoque", itemParaCadastrar);

        // then
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals("OLEO-5W30", cadastro.getBody().get("codigo"));
        assertEquals(true, cadastro.getBody().get("ativo"));

        var itemId = cadastro.getBody().get("id").toString();

        // when
        var consulta = getMap("/estoque/" + itemId);

        // then
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(itemId, consulta.getBody().get("id"));
        assertEquals("Oleo 5W30", consulta.getBody().get("descricao"));

        // given
        var itemParaAtualizar = Map.of(
                "descricao", "Oleo 5W30 sintetico",
                "valorUnitario", 55.90);

        // when
        put("/estoque/" + itemId, itemParaAtualizar);
        var atualizado = getMap("/estoque/" + itemId);

        // then
        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Oleo 5W30 sintetico", atualizado.getBody().get("descricao"));

        // given
        var inclusao = Map.of("quantidade", 5.000);

        // when
        var itemComInclusao = postMap("/estoque/" + itemId + "/inclusoes", inclusao);

        // then
        assertEquals(HttpStatus.OK, itemComInclusao.getStatusCode());
        assertNotNull(itemComInclusao.getBody());
        assertEquals(15.0, ((Number) itemComInclusao.getBody().get("quantidadeDisponivel")).doubleValue());

        // given
        var baixa = Map.of("quantidade", 3.000);

        // when
        var itemComBaixa = postMap("/estoque/" + itemId + "/baixas", baixa);

        // then
        assertEquals(HttpStatus.OK, itemComBaixa.getStatusCode());
        assertNotNull(itemComBaixa.getBody());
        assertEquals(12.0, ((Number) itemComBaixa.getBody().get("quantidadeDisponivel")).doubleValue());

        // when
        var listagem = getList("/estoque");

        // then
        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertEquals(1, listagem.getBody().size());
        assertEquals(itemId, ((Map<?, ?>) listagem.getBody().get(0)).get("id"));

        // when
        var exclusao = delete("/estoque/" + itemId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        // when
        var consultaDepoisDaExclusao = getMap("/estoque/" + itemId);

        // then
        assertEquals(HttpStatus.OK, consultaDepoisDaExclusao.getStatusCode());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertEquals(false, consultaDepoisDaExclusao.getBody().get("ativo"));
    }
}

