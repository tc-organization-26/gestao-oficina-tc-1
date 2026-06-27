package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClienteJpaEntityTest {
    @Test
    void gettersRetornamDados() {
        var id = UUID.randomUUID();
        var criadoEm = OffsetDateTime.now();
        var atualizadoEm = OffsetDateTime.now();
        var entity = new ClienteJpaEntity(id, "Maria", "12345678901", "11", "maria@email.com", true, criadoEm, atualizadoEm);

        assertEquals(id, entity.getId());
        assertEquals("Maria", entity.getNome());
        assertEquals("12345678901", entity.getCpfCnpj());
        assertTrue(entity.getAtivo());
        assertEquals(criadoEm, entity.getCriadoEm());
    }
}