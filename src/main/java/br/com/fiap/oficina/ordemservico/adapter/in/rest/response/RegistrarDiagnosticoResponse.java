package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import br.com.fiap.oficina.ordemservico.domain.model.Diagnostico;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RegistrarDiagnosticoResponse(
        UUID id,
        String descricao,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static RegistrarDiagnosticoResponse from(Diagnostico diagnostico) {
        return new RegistrarDiagnosticoResponse(
                diagnostico.id(),
                diagnostico.descricao(),
                diagnostico.criadoEm(),
                diagnostico.atualizadoEm()
        );
    }
}
