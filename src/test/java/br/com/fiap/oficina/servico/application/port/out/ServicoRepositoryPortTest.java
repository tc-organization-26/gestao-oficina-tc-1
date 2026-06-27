package br.com.fiap.oficina.servico.application.port.out;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServicoRepositoryPortTest {
    @Test
    void ehInterface() {
        assertTrue(ServicoRepositoryPort.class.isInterface());
    }
}