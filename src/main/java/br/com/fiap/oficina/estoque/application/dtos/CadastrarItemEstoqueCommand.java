package br.com.fiap.oficina.estoque.application.dtos;

import java.math.BigDecimal;

public record CadastrarItemEstoqueCommand(
        String codigo,
        String descricao,
        BigDecimal valorUnitario,
        BigDecimal quantidadeInicial
) {
}