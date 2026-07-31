package br.com.fiap.oficina.veiculo.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class VeiculoIdTest {
    @Test
    void novoGeraIdentificador() {
        assertNotNull(VeiculoId.novo().value());
    }

    @Test
    void mantemValorInformado() {
        var id = UUID.randomUUID();
        assertEquals(id, new VeiculoId(id).value());
    }
}