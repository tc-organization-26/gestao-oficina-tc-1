package br.com.fiap.oficina.ordemservico.application.port.out;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepositoryPort {
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId);
    List<OrdemServico> buscarPorClienteOrdenado(UUID clienteId);
    List<OrdemServico> buscarPorStatusOrdenado(Integer statusOrdemServico);
    List<OrdemServico> buscarTodosOrdenado();
}