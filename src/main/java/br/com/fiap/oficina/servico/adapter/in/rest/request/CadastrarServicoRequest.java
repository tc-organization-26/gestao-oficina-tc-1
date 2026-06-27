package br.com.fiap.oficina.servico.adapter.in.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CadastrarServicoRequest(

        @NotBlank(message = "Código do serviço é obrigatório.")
        String codigo,

        @NotBlank(message = "Descrição do serviço é obrigatória.")
        String descricao,

        @NotNull(message = "Valor unitário é obrigatório.")
        @DecimalMin(value = "0.00", message = "Valor unitário deve ser maior ou igual a zero.")
        BigDecimal valorUnitario,

        @NotNull(message = "Tempo estimado é obrigatório.")
        @Positive(message = "Tempo estimado deve ser maior que zero.")
        Integer tempoEstimadoMinutos
) {
}