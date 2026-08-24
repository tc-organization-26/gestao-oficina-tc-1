package br.com.fiap.oficina.estoque.application.dtos;

import java.math.BigDecimal;

public record BaixarItemEstoqueCommand(String codigo, BigDecimal quantidade) {
}