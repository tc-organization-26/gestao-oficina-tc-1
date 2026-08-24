package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrcamentoItemPecaResponse(
        UUID itemEstoqueId,
        BigDecimal quantidade
) {}