package br.com.fiap.oficina.ordemservico.domain.events;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.events.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FaltaPecaEstoqueEventTest {
    @Test
    void construtorCompletoExpoeCampos() {
        var ordemServicoId = UUID.randomUUID();
        var itemEstoqueId = UUID.randomUUID();
        var occurredAt = Instant.now();

        var event = new FaltaPecaEstoqueEvent(ordemServicoId, itemEstoqueId, BigDecimal.valueOf(3), occurredAt);

        assertInstanceOf(DomainEvent.class, event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(itemEstoqueId, event.itemEstoqueId());
        assertEquals(BigDecimal.valueOf(3), event.quantidadeSolicitada());
        assertEquals(occurredAt, event.occurredAt());
    }

    @Test
    void construtorSimplesDefineMomentoDoEvento() {
        var event = new FaltaPecaEstoqueEvent(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE);

        assertNotNull(event.occurredAt());
    }
}
