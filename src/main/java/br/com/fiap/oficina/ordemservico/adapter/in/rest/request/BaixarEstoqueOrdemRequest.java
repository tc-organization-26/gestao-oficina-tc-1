package br.com.fiap.oficina.ordemservico.adapter.in.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record BaixarEstoqueOrdemRequest(
        @NotNull UUID itemEstoqueId,
        @NotNull @Positive Double quantidade
) {}
