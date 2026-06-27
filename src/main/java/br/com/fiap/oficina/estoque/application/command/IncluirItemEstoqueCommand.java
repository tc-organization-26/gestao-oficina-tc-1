package br.com.fiap.oficina.estoque.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record IncluirItemEstoqueCommand(UUID itemEstoqueId, BigDecimal quantidade) {
}