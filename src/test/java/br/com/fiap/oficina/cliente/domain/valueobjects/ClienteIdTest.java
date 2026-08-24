package br.com.fiap.oficina.cliente.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClienteIdTest {
    @Test
    void novoGeraIdentificador() {
        assertNotNull(ClienteId.novo().value());
    }

    @Test
    void mantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new ClienteId(id).value());
    }
}