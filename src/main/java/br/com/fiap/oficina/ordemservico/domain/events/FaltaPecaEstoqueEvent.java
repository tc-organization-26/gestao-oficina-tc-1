package br.com.fiap.oficina.ordemservico.domain.events;

import br.com.fiap.oficina.shared.domain.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FaltaPecaEstoqueEvent(
        UUID ordemServicoId,
        UUID itemEstoqueId,
        BigDecimal quantidadeSolicitada,
        Instant occurredAt) implements DomainEvent {

    public FaltaPecaEstoqueEvent(UUID ordemServicoId, UUID itemEstoqueId, BigDecimal quantidadeSolicitada) {
        this(ordemServicoId, itemEstoqueId, quantidadeSolicitada, Instant.now());
    }
}
