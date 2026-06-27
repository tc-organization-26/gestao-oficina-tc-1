package br.com.fiap.oficina.ordemservico.application.port.out;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;

import java.util.Optional;

public interface OrdemServicoRepositoryPort {
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId);
}