package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AdicionarItemPecaOrcamentoRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade
) {}