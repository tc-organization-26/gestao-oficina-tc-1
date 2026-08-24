package br.com.fiap.oficina.cliente.domain.entities;

import br.com.fiap.oficina.cliente.domain.valueobjects.*;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

class ClienteTest {
    @Test
    void criarPreencheDadosPadrao() {
        var cliente = Cliente.criar(CpfCnpj.novo("12345678901"), " Maria ", "MARIA@EMAIL.COM", "11999999999");

        assertNotNull(cliente.id());
        assertEquals("Maria", cliente.nome());
        assertEquals("maria@email.com", cliente.email());
        assertTrue(cliente.ativo());
        assertNotNull(cliente.criadoEm());
        assertNotNull(cliente.atualizadoEm());
    }

    @Test
    void atualizarAlteraDadosPermitidos() {
        var cliente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11999999999");

        cliente.atualizar("Joao", "JOAO@EMAIL.COM", "11888888888");

        assertEquals("Joao", cliente.nome());
        assertEquals("joao@email.com", cliente.email());
        assertEquals("11888888888", cliente.telefone());
    }

    @Test
    void rejeitaNomeObrigatorio() {
        assertThrows(DomainException.class, () -> Cliente.criar(CpfCnpj.novo("12345678901"), " ", "a@b.com", "11"));
    }
}