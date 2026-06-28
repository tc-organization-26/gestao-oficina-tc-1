package br.com.fiap.oficina.ordemservico.application.command;

import java.util.UUID;

public record RegistrarDiagnosticoCommand(
        UUID ordemServicoId,
        String descricao
) {
}
