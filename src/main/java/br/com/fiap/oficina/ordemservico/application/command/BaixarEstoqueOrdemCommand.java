package br.com.fiap.oficina.ordemservico.application.command;

import java.util.UUID;

public record BaixarEstoqueOrdemCommand(UUID ordemId, UUID itemEstoqueId, double quantidade) {}
