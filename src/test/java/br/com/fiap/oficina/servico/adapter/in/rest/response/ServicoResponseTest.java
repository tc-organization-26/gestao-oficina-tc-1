package br.com.fiap.oficina.servico.adapter.in.rest.response;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.servico.domain.model.Servico;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ServicoResponseTest {
    @Test
    void criaResponseAPartirDoDominio() {
        var servico = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);

        var response = ServicoResponse.from(servico);

        assertEquals(servico.id().value(), response.id());
        assertEquals("TROCA", response.codigo());
        assertEquals(BigDecimal.TEN, response.valorUnitario());
    }
}