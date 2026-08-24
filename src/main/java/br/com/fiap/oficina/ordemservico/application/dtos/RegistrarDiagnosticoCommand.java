package br.com.fiap.oficina.ordemservico.application.dtos;

import java.util.UUID;

public record RegistrarDiagnosticoCommand(
        UUID ordemServicoId,
        String descricao
) {
}
