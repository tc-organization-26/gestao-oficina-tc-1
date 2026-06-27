package br.com.fiap.oficina.veiculo.domain.model;

import java.util.UUID;

public record VeiculoId(UUID value) {
    public static VeiculoId novo() {
        return new VeiculoId(UUID.randomUUID());
    }
}
