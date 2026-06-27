package br.com.fiap.oficina.estoque.adapter.in.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarItemEstoqueRequest(
        @NotBlank String descricao,
        @NotNull @DecimalMin(value = "0.00") BigDecimal valorUnitario
) {
}