package br.com.fiap.oficina.ordemservico.adapter.in.rest.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CriarOrdemServicoRequest(
        @NotNull(message = "Cliente e obrigatorio.") UUID clienteId,
        @NotNull(message = "Veiculo e obrigatorio.") UUID veiculoId,
        String anotacoes
) {
}