package br.com.fiap.oficina.estoque.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemEstoqueIdTest {
    @Test
    void novoGeraIdentificador() {
        assertNotNull(ItemEstoqueId.novo().value());
    }

    @Test
    void mantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new ItemEstoqueId(id).value());
    }
}
