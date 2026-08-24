package br.com.fiap.oficina.ordemservico.domain.events;

import br.com.fiap.oficina.shared.domain.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrdemServicoFinalizadaEvent(
        UUID ordemServicoId,
        UUID clienteId,
        Instant occurredAt) implements DomainEvent {

    public OrdemServicoFinalizadaEvent(UUID ordemServicoId, UUID clienteId) {
        this(ordemServicoId, clienteId, Instant.now());
    }
}
