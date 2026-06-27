package br.com.fiap.oficina.shared.domain;

import java.time.Instant;

/** Contract shared by domain events. */
public interface DomainEvent {
    Instant occurredAt();
}
