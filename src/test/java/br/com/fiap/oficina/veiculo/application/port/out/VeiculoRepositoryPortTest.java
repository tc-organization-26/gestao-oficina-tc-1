package br.com.fiap.oficina.veiculo.application.port.out;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VeiculoRepositoryPortTest {
    @Test
    void ehInterface() {
        assertTrue(VeiculoRepositoryPort.class.isInterface());
    }
}