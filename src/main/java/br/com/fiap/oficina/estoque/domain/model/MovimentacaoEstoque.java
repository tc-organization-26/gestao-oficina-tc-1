package br.com.fiap.oficina.estoque.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimentacaoEstoque(
        ItemEstoqueId itemEstoqueId,
        TipoMovimentacao tipo,
        BigDecimal quantidade,
        OffsetDateTime ocorridoEm
) {
}