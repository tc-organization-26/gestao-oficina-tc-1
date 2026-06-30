package br.com.fiap.oficina.ordemservico.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoFechadoEventTest {
    @Test
    void construtorCompletoExpoeCampos() {
        var ordemServicoId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var occurredAt = Instant.now();

        var event = new OrcamentoFechadoEvent(ordemServicoId, clienteId, occurredAt);

        assertInstanceOf(DomainEvent.class, event);
        assertEquals(ordemServicoId, event.ordemServicoId());
        assertEquals(clienteId, event.clienteId());
        assertEquals(occurredAt, event.occurredAt());
    }

    @Test
    void construtorSimplesDefineMomentoDoEvento() {
        var event = new OrcamentoFechadoEvent(UUID.randomUUID(), UUID.randomUUID());

        assertNotNull(event.occurredAt());
    }
}
