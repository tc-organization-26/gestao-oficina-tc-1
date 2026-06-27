package br.com.fiap.oficina.servico.adapter.in.rest.request;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CadastrarServicoRequestTest {
    @Test
    void guardaDados() {
        var request = new CadastrarServicoRequest("TROCA", "Troca", BigDecimal.TEN, 60);

        assertEquals("TROCA", request.codigo());
        assertEquals(BigDecimal.TEN, request.valorUnitario());
    }
}