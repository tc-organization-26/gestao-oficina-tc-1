package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;

import java.util.List;
import java.util.UUID;

public interface ConsultarOrdemServicoUseCase {
    OrdemServico consultarPorId(OrdemServicoId ordemServicoId);
    List<OrdemServico> consultarPorCliente(UUID clienteId);
    List<OrdemServico> consultarOrdens(StatusOrdemServico status);
    String consultarTempoMedioExecucao();
}