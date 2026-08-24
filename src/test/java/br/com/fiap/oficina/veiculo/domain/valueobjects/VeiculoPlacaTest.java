package br.com.fiap.oficina.veiculo.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

class VeiculoPlacaTest {
    @Test
    void normalizaPlaca() {
        assertEquals("ABC1D23", VeiculoPlaca.novo("abc-1d23").value());
    }

    @Test
    void rejeitaPlacaInvalida() {
        assertThrows(DomainException.class, () -> VeiculoPlaca.novo("ABC"));
    }
}