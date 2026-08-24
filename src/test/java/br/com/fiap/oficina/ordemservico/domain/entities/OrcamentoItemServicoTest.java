package br.com.fiap.oficina.ordemservico.domain.entities;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoItemServicoTest {

    @Test
    void criaItemServicoValido() {
        var servicoId = new ServicoId(UUID.randomUUID());

        var item = new OrcamentoItemServico(servicoId, java.math.BigDecimal.valueOf(2));

        assertEquals(servicoId, item.servicoId());
        assertEquals(java.math.BigDecimal.valueOf(2), item.quantidade());
    }

    @Test
    void rejeitaServicoNulo() {
        assertThrows(DomainException.class, () -> new OrcamentoItemServico(null, java.math.BigDecimal.ONE));
    }

    @Test
    void rejeitaQuantidadeMenorOuIgualAZero() {
        var servicoId = new ServicoId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new OrcamentoItemServico(servicoId, java.math.BigDecimal.ZERO));
        assertThrows(DomainException.class, () -> new OrcamentoItemServico(servicoId, java.math.BigDecimal.valueOf(-1)));
    }
}
