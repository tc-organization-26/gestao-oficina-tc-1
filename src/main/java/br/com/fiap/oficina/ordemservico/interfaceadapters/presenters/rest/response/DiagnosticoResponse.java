package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import br.com.fiap.oficina.ordemservico.domain.entities.Diagnostico;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DiagnosticoResponse(
        UUID id,
        String descricao,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static DiagnosticoResponse from(Diagnostico diagnostico) {
        if (diagnostico == null) {
            return null;
        }
        return new DiagnosticoResponse(
                diagnostico.id(),
                diagnostico.descricao(),
                diagnostico.criadoEm(),
                diagnostico.atualizadoEm());
    }
}
