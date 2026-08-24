package br.com.fiap.oficina.shared.domain.entities;

/** Base marker for domain entities. */
public abstract class Entity<ID> {
    public abstract ID id();
}
