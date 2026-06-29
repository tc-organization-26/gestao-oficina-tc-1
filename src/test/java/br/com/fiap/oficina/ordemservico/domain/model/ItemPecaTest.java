package br.com.fiap.oficina.ordemservico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemPecaTest {

    @Test
    void criaItemPecaValido() {
        var itemEstoqueId = new ItemEstoqueId(UUID.randomUUID());

        var peca = new ItemPeca(itemEstoqueId, 3.0);

        assertEquals(itemEstoqueId, peca.itemEstoqueId());
        assertEquals(3.0, peca.quantidade());
    }

    @Test
    void rejeitaItemEstoqueNulo() {
        assertThrows(DomainException.class, () -> new ItemPeca(null, 1.0));
    }

    @Test
    void rejeitaQuantidadeMenorOuIgualAZero() {
        var itemEstoqueId = new ItemEstoqueId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new ItemPeca(itemEstoqueId, 0));
        assertThrows(DomainException.class, () -> new ItemPeca(itemEstoqueId, -1));
    }
}
