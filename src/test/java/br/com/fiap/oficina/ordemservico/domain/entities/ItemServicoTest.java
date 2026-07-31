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

        var item = new ItemServico(servicoId, 2.0);

        assertEquals(servicoId, item.servicoId());
        assertEquals(2.0, item.quantidade());
    }

    @Test
    void rejeitaServicoNulo() {
        assertThrows(DomainException.class, () -> new ItemServico(null, 1.0));
    }

    @Test
    void rejeitaQuantidadeMenorOuIgualAZero() {
        var servicoId = new ServicoId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new ItemServico(servicoId, 0));
        assertThrows(DomainException.class, () -> new ItemServico(servicoId, -1));
    }
}
