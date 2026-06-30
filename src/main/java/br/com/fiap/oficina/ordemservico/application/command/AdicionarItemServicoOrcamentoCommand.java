package br.com.fiap.oficina.ordemservico.application.command;

import java.util.UUID;

public record AdicionarItemServicoOrcamentoCommand(UUID ordemId, String servicoCodigo, double quantidade) {}