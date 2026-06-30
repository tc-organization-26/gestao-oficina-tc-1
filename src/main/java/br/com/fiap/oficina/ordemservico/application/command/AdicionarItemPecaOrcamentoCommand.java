package br.com.fiap.oficina.ordemservico.application.command;

import java.util.UUID;

public record AdicionarItemPecaOrcamentoCommand(UUID ordemId, String itemEstoqueCodigo, double quantidade) {}