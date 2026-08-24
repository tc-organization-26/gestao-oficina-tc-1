package br.com.fiap.oficina.servico.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

class ServicoCodigoTest {
    @Test
    void criaCodigoValido() {
        assertEquals("TROCA", ServicoCodigo.novo("TROCA").value());
    }

    @Test
    void rejeitaCodigoVazio() {
        assertThrows(DomainException.class, () -> ServicoCodigo.novo(" "));
    }
}