package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemServicoTest {

    @Test
    void criaItemServicoValido() {
        var servicoId = new ServicoId(UUID.randomUUID());

        var item = new ItemServico(servicoId, java.math.BigDecimal.valueOf(2));

        assertEquals(servicoId, item.servicoId());
        assertEquals(java.math.BigDecimal.valueOf(2), item.quantidade());
    }

    @Test
    void rejeitaServicoNulo() {
        assertThrows(DomainException.class, () -> new ItemServico(null, java.math.BigDecimal.ONE));
    }

    @Test
    void rejeitaQuantidadeMenorOuIgualAZero() {
        var servicoId = new ServicoId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new ItemServico(servicoId, java.math.BigDecimal.ZERO));
        assertThrows(DomainException.class, () -> new ItemServico(servicoId, java.math.BigDecimal.valueOf(-1)));
    }
}
