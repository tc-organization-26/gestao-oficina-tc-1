package br.com.fiap.oficina.estoque.domain.entities;

import br.com.fiap.oficina.estoque.domain.enums.*;

import br.com.fiap.oficina.estoque.domain.valueobjects.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimentacaoEstoque(
        ItemEstoqueId itemEstoqueId,
        TipoMovimentacao tipo,
        BigDecimal quantidade,
        OffsetDateTime ocorridoEm
) {
}