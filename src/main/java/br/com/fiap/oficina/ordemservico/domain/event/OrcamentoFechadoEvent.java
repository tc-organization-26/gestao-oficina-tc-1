package br.com.fiap.oficina.ordemservico.domain.event;

import br.com.fiap.oficina.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrcamentoFechadoEvent(
        UUID ordemServicoId,
        UUID clienteId,
        Instant occurredAt) implements DomainEvent {

    public OrcamentoFechadoEvent(UUID ordemServicoId, UUID clienteId) {
        this(ordemServicoId, clienteId, Instant.now());
    }
}
