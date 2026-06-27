package br.com.fiap.oficina.servico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServicoIdTest {
    @Test
    void novoGeraIdentificador() {
        assertNotNull(ServicoId.novo().value());
    }

    @Test
    void mantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new ServicoId(id).value());
    }
}