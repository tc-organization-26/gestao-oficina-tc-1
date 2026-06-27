package br.com.fiap.oficina.veiculo.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtualizarVeiculoRequest(
        @NotBlank(message = "Modelo do veículo é obrigatório.")
        String modelo,

        @NotBlank(message = "Marca do veículo é obrigatória.")
        String marca,

        @NotNull(message = "Ano do veículo é obrigatório.")
        @Positive(message = "Ano do veículo deve ser maior que zero.")
        Integer ano
) {
}