package br.com.fiap.oficina.autenticacao.domain.model;

import java.util.UUID;

public record UsuarioId(UUID value) {
    public static UsuarioId novo() {
        return new UsuarioId(UUID.randomUUID());
    }
}