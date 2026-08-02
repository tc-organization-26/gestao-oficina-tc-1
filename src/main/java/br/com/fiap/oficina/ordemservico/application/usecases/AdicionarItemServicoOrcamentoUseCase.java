package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface AdicionarItemServicoOrcamentoUseCase {
    OrdemServico adicionarItemServico(AdicionarItemServicoOrcamentoCommand command);
}
