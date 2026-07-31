package br.com.fiap.oficina.ordemservico.domain.events;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.events.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FaltaPecaEstoqueEventTest {
    @Test
    void construtorCompletoExpoeCampos() {
        var ordemServicoId = UUID.randomUUID();
        var itemEstoqueId = UUID.randomUUID();
        var occurredAt = Instant.now();

        var event = new FaltaPecaEstoqueEvent(ordemServicoId, itemEstoqueId, 3.0, occurredAt);

        assertInstanceOf(DomainEvent.class, event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(itemEstoqueId, event.itemEstoqueId());
        assertEquals(3.0, event.quantidadeSolicitada());
        assertEquals(occurredAt, event.occurredAt());
    }

    @Test
    void construtorSimplesDefineMomentoDoEvento() {
        var event = new FaltaPecaEstoqueEvent(UUID.randomUUID(), UUID.randomUUID(), 1.0);

        assertNotNull(event.occurredAt());
    }
}
