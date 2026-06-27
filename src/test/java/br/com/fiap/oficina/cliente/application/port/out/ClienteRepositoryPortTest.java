package br.com.fiap.oficina.cliente.application.port.out;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteRepositoryPortTest {
    @Test
    void ehInterface() {
        assertTrue(ClienteRepositoryPort.class.isInterface());
    }
}