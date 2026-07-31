package br.com.fiap.oficina.ordemservico.application.dtos;

import java.util.UUID;

public record CriarOrdemServicoCommand(
        UUID clienteId,
        UUID veiculoId,
        String anotacoes
) {
}