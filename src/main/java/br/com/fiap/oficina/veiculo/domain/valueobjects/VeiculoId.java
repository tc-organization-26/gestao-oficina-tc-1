package br.com.fiap.oficina.veiculo.domain.valueobjects;

import java.util.UUID;

public record VeiculoId(UUID value) {
    public static VeiculoId novo() {
        return new VeiculoId(UUID.randomUUID());
    }
}
