package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;

import java.util.List;
import java.util.UUID;

public interface ConsultarOrdemServicoUseCase {
    OrdemServico consultarPorId(OrdemServicoId ordemServicoId);
    List<OrdemServico> consultarPorCliente(UUID clienteId);
    List<OrdemServico> consultarOrdens(StatusOrdemServico status);
    String consultarTempoMedioExecucao();
}