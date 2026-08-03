package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemPecaTest {

    @Test
    void criaItemPecaValido() {
        var itemEstoqueId = new ItemEstoqueId(UUID.randomUUID());

        var peca = new ItemPeca(itemEstoqueId, java.math.BigDecimal.valueOf(3));

        assertEquals(itemEstoqueId, peca.itemEstoqueId());
        assertEquals(java.math.BigDecimal.valueOf(3), peca.quantidade());
    }

    @Test
    void rejeitaItemEstoqueNulo() {
        assertThrows(DomainException.class, () -> new ItemPeca(null, java.math.BigDecimal.ONE));
    }

    @Test
    void rejeitaQuantidadeMenorOuIgualAZero() {
        var itemEstoqueId = new ItemEstoqueId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new ItemPeca(itemEstoqueId, java.math.BigDecimal.ZERO));
        assertThrows(DomainException.class, () -> new ItemPeca(itemEstoqueId, java.math.BigDecimal.valueOf(-1)));
    }
}
