package br.com.fiap.oficina.estoque.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MovimentarEstoqueRequest(
        @NotNull(message = "Quantidade e obrigatoria.") @DecimalMin(value = "0.001") BigDecimal quantidade
) {
}