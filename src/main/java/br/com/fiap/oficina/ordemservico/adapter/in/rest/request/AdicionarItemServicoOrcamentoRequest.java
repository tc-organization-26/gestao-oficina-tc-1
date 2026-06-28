package br.com.fiap.oficina.ordemservico.adapter.in.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdicionarItemServicoOrcamentoRequest(
        @NotNull java.util.UUID servicoId,
        @NotNull @Min(1) Double quantidade
) {}
