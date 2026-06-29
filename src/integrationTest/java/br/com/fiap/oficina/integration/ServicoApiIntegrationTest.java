package br.com.fiap.oficina.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ServicoApiIntegrationTest extends AbstractApiIntegrationSupport {

    @Test
    void deveCadastrarConsultarAtualizarListarEExcluirServico() {
        // given
        var servicoParaCadastrar = Map.of(
                "codigo", "TROCA-OLEO",
                "descricao", "Troca de oleo",
                "valorUnitario", 120.50,
                "tempoEstimadoMinutos", 60);

        // when
        var cadastro = postMap("/servicos", servicoParaCadastrar);

        // then
        assertEquals(HttpStatus.CREATED, cadastro.getStatusCode());
        assertNotNull(cadastro.getBody());
        assertNotNull(cadastro.getBody().get("id"));
        assertEquals("TROCA-OLEO", cadastro.getBody().get("codigo"));

        var servicoId = cadastro.getBody().get("id").toString();

        // when
        var consulta = getMap("/servicos/" + servicoId);

        // then
        assertEquals(HttpStatus.OK, consulta.getStatusCode());
        assertNotNull(consulta.getBody());
        assertEquals(servicoId, consulta.getBody().get("id"));
        assertEquals("Troca de oleo", consulta.getBody().get("descricao"));

        // given
        var servicoParaAtualizar = Map.of(
                "descricao", "Troca de oleo premium",
                "valorUnitario", 180.00,
                "tempoEstimadoMinutos", 90);

        // when
        put("/servicos/" + servicoId, servicoParaAtualizar);
        var atualizado = getMap("/servicos/" + servicoId);

        // then
        assertEquals(HttpStatus.OK, atualizado.getStatusCode());
        assertNotNull(atualizado.getBody());
        assertEquals("Troca de oleo premium", atualizado.getBody().get("descricao"));
        assertEquals(90, atualizado.getBody().get("tempoEstimadoMinutos"));

        // when
        var listagem = getList("/servicos");

        // then
        assertEquals(HttpStatus.OK, listagem.getStatusCode());
        assertNotNull(listagem.getBody());
        assertEquals(1, listagem.getBody().size());
        assertEquals(servicoId, ((Map<?, ?>) listagem.getBody().get(0)).get("id"));

        // when
        var exclusao = delete("/servicos/" + servicoId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, exclusao.getStatusCode());

        // when
        var consultaDepoisDaExclusao = getMap("/servicos/" + servicoId);

        // then
        assertEquals(422, consultaDepoisDaExclusao.getStatusCode().value());
        assertNotNull(consultaDepoisDaExclusao.getBody());
        assertEquals("Servico não encontrado.", consultaDepoisDaExclusao.getBody().get("message"));
    }
}

