package br.com.fiap.oficina.servico.adapter.in.rest.response;

import br.com.fiap.oficina.servico.domain.model.Servico;
import java.util.UUID;

public record AtualizarServicoResponse(
        UUID id,
        String codigo,
        String descricao,
        double valorUnitario,
        int tempoEstimadoMinutos
) {
    public static AtualizarServicoResponse from(Servico servico) {
        return new AtualizarServicoResponse(
                servico.id().value(),
                servico.codigo(),
                servico.descricao(),
                servico.valorUnitario().doubleValue(),
                servico.tempoEstimadoMinutos()
        );
    }
}
