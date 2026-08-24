package br.com.fiap.oficina.estoque.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CadastrarItemEstoqueRequest(
        @NotBlank(message = "Codigo do item e obrigatorio.") String codigo,
        @NotBlank(message = "Descricao do item e obrigatoria.") String descricao,
        @NotNull(message = "Valor unitario e obrigatorio.") @DecimalMin(value = "0.00") BigDecimal valorUnitario,
        @NotNull(message = "Quantidade inicial e obrigatoria.") @DecimalMin(value = "0.00") BigDecimal quantidadeInicial
) {
}