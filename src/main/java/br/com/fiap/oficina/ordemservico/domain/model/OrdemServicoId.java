package br.com.fiap.oficina.ordemservico.domain.model;

import java.util.UUID;

public record OrdemServicoId(UUID value) {
    public static OrdemServicoId novo() {
        return new OrdemServicoId(UUID.randomUUID());
    }
}