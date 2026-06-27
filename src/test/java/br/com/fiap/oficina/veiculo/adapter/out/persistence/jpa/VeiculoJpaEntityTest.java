package br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VeiculoJpaEntityTest {
    @Test
    void gettersRetornamDados() {
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var criadoEm = OffsetDateTime.now();
        var atualizadoEm = OffsetDateTime.now();
        var entity = new VeiculoJpaEntity(id, clienteId, "ABC1D23", "Toyota", "Corolla", 2020, criadoEm, atualizadoEm);

        assertEquals(id, entity.getId());
        assertEquals(clienteId, entity.getClienteId());
        assertEquals("ABC1D23", entity.getPlaca());
        assertEquals("Toyota", entity.getMarca());
        assertEquals(criadoEm, entity.getCriadoEm());
    }
}