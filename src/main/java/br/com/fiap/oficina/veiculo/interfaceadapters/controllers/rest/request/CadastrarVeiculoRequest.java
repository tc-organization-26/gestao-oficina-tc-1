package br.com.fiap.oficina.veiculo.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CadastrarVeiculoRequest(
        @NotBlank
        String clienteId,
        @NotBlank
        String placa,
        @NotBlank
        String marca,
        @NotBlank
        String modelo,
        Integer ano
) {
}