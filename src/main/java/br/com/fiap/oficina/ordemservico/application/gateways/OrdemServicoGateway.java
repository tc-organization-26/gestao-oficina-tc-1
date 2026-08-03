package br.com.fiap.oficina.ordemservico.application.gateways;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoGateway {
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId);
    List<OrdemServico> buscarPorClienteOrdenado(UUID clienteId);
    List<OrdemServico> buscarPorStatusOrdenado(Integer statusOrdemServico);
    List<OrdemServico> buscarTodosOrdenado();
}