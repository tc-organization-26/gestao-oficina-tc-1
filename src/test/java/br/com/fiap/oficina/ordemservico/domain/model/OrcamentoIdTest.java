package br.com.fiap.oficina.ordemservico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoIdTest {
    @Test
    void fromMantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, OrcamentoId.from(id).value());
    }

    @Test
    void construtorMantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new OrcamentoId(id).value());
    }
}
