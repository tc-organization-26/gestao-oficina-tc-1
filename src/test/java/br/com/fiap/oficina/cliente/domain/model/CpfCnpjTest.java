package br.com.fiap.oficina.cliente.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.DomainException;
import org.junit.jupiter.api.Test;

class CpfCnpjTest {
    @Test
    void normalizaCpfCnpjRemovendoMascara() {
        assertEquals("12345678901", CpfCnpj.novo("123.456.789-01").value());
    }

    @Test
    void rejeitaValorInvalido() {
        assertThrows(DomainException.class, () -> CpfCnpj.novo("123"));
    }
}