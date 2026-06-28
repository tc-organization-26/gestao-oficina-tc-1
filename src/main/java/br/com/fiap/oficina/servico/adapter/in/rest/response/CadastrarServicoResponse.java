package br.com.fiap.oficina.servico.adapter.in.rest.response;

import br.com.fiap.oficina.servico.domain.model.Servico;
import java.util.UUID;

public record CadastrarServicoResponse(
        UUID id,
        String codigo,
        String descricao,
        double valorUnitario,
        int tempoEstimadoMinutos
) {
    public static CadastrarServicoResponse from(Servico servico) {
        return new CadastrarServicoResponse(
                servico.id().value(),
                servico.codigo(),
                servico.descricao(),
                servico.valorUnitario().doubleValue(),
                servico.tempoEstimadoMinutos()
        );
    }
}
