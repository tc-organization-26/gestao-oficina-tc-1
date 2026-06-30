package br.com.fiap.oficina.estoque.application.command;

import java.math.BigDecimal;

public record BaixarItemEstoqueCommand(String codigo, BigDecimal quantidade) {
}