package br.com.fiap.oficina.ordemservico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrdemServicoIdTest {
    @Test
    void novoGeraIdentificador() {
        assertNotNull(OrdemServicoId.novo().value());
    }

    @Test
    void mantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new OrdemServicoId(id).value());
    }
}
