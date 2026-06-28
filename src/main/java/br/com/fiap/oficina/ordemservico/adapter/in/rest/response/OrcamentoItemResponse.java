package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import java.util.UUID;

public record OrcamentoItemResponse(
        UUID servicoId,
        Double quantidade
) {}
