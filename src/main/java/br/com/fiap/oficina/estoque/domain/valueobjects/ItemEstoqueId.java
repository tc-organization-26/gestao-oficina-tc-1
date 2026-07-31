package br.com.fiap.oficina.estoque.domain.valueobjects;

import java.util.UUID;

public record ItemEstoqueId(UUID value) {
    public static ItemEstoqueId novo() {
        return new ItemEstoqueId(UUID.randomUUID());
    }
}