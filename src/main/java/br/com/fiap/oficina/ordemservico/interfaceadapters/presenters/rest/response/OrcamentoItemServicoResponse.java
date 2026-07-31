package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import java.util.UUID;

public record OrcamentoItemServicoResponse(
        UUID servicoId,
        Double quantidade
) {}