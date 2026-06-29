package br.com.fiap.oficina.ordemservico.domain.event;

import br.com.fiap.oficina.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record FaltaPecaEstoqueEvent(
        UUID ordemServicoId,
        UUID itemEstoqueId,
        double quantidadeSolicitada,
        Instant occurredAt) implements DomainEvent {

    public FaltaPecaEstoqueEvent(UUID ordemServicoId, UUID itemEstoqueId, double quantidadeSolicitada) {
        this(ordemServicoId, itemEstoqueId, quantidadeSolicitada, Instant.now());
    }
}
