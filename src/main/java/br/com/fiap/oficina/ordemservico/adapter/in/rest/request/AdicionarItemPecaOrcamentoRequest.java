package br.com.fiap.oficina.ordemservico.adapter.in.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AdicionarItemPecaOrcamentoRequest(
        @NotNull UUID itemEstoqueId,
        @NotNull @Min(1) Double quantidade
) {}