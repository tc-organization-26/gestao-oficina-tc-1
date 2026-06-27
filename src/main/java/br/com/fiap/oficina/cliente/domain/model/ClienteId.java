package br.com.fiap.oficina.cliente.domain.model;

import java.util.UUID;

public record ClienteId(UUID value) {
    public static ClienteId novo() {
        return new ClienteId(UUID.randomUUID());
    }
}
