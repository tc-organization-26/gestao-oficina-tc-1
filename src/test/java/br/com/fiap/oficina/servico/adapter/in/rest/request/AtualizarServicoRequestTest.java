package br.com.fiap.oficina.servico.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AtualizarServicoRequestTest {
    @Test
    void guardaDados() {
        var request = new AtualizarServicoRequest("Troca", BigDecimal.TEN, 60);

        assertEquals("Troca", request.descricao());
        assertEquals(60, request.tempoEstimadoMinutos());
    }
}