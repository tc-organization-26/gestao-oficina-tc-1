package br.com.fiap.oficina.servico.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarServicoCommand(
                UUID servicoId,
                String descricao,
                BigDecimal valorUnitario,
                Integer tempoEstimadoMinutos) {
}