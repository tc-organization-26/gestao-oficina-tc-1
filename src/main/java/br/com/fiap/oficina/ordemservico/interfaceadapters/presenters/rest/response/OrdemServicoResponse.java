package br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrdemServicoResponse(
        UUID id,
        Long numero,
        UUID clienteId,
        UUID veiculoId,
        StatusOrdemServico status,
        String anotacoes,
        DiagnosticoResponse diagnostico,
        OffsetDateTime dataRecebimento,
        OffsetDateTime inicioExecucaoEm,
        OffsetDateTime finalizadaEm,
        OffsetDateTime entregueEm,
        OrcamentoResponse orcamento,
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
                DiagnosticoResponse.from(ordemServico.diagnostico()),
                ordemServico.dataRecebimento(),
                ordemServico.inicioExecucaoEm(),
                ordemServico.finalizadaEm(),
                ordemServico.entregueEm(),
                ordemServico.orcamento() != null ? OrcamentoResponse.from(ordemServico.orcamento()) : null,
                ordemServico.pago());
    }
}