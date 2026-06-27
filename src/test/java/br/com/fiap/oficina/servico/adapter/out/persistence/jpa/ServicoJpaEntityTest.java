package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServicoJpaEntityTest {
    @Test
    void gettersRetornamDados() {
        var id = UUID.randomUUID();
        var criadoEm = OffsetDateTime.now();
        var atualizadoEm = OffsetDateTime.now();
        var entity = new ServicoJpaEntity(id, "TROCA", "Troca", BigDecimal.TEN, 60, true, criadoEm, atualizadoEm);

        assertEquals(id, entity.getId());
        assertEquals("TROCA", entity.getCodigo());
        assertEquals(BigDecimal.TEN, entity.getValorUnitario());
        assertTrue(entity.isAtivo());
        assertEquals(criadoEm, entity.getCriadoEm());
    }
}