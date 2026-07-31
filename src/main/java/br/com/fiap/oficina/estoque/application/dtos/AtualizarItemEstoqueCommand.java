package br.com.fiap.oficina.estoque.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarItemEstoqueCommand(
        UUID itemEstoqueId,
        String descricao,
        BigDecimal valorUnitario
) {
}