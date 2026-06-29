package br.com.fiap.oficina.ordemservico.adapter.in.rest.response;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrdemServicoResponse(
        UUID id,
        Long numero,
        UUID clienteId,
        UUID veiculoId,
        StatusOrdemServico status,
        String anotacoes,
        OffsetDateTime dataRecebimento,
        OffsetDateTime inicioExecucaoEm,
        OffsetDateTime finalizadaEm,
        OffsetDateTime entregueEm,
        boolean pago
) {
    public static OrdemServicoResponse from(OrdemServico ordemServico) {
        return new OrdemServicoResponse(
                ordemServico.id().value(),
                ordemServico.numero(),
                ordemServico.clienteId().value(),
                ordemServico.veiculoId().value(),
                ordemServico.status(),
                ordemServico.anotacoes(),
                ordemServico.dataRecebimento(),
                ordemServico.inicioExecucaoEm(),
                ordemServico.finalizadaEm(),
                ordemServico.entregueEm(),
                ordemServico.pago());
    }
}