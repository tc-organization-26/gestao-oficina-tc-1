package br.com.fiap.oficina.servico.adapter.in.rest.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.fiap.oficina.servico.domain.model.Servico;

public record ServicoResponse(
        UUID id,
        String codigo,
        String descricao,
        BigDecimal valorUnitario,
        Integer tempoEstimadoMinutos,
        boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static ServicoResponse from(Servico servico) {
        return new ServicoResponse(
                servico.id().value(),
                servico.codigo(),
                servico.descricao(),
                servico.valorUnitario(),
                servico.tempoEstimadoMinutos(),
                servico.ativo(),
                servico.criadoEm(),
                servico.atualizadoEm());
    }
}
