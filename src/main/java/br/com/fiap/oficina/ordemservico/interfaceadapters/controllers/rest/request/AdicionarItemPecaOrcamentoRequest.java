package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdicionarItemPecaOrcamentoRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotNull @Min(1) Double quantidade
) {}