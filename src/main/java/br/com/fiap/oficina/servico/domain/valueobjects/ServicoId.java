package br.com.fiap.oficina.servico.domain.valueobjects;

import java.util.UUID;

public record ServicoId(UUID value) {
    public static ServicoId novo() {
        return new ServicoId(UUID.randomUUID());
    }
}
