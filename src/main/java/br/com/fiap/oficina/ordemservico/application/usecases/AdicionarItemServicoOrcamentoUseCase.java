package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;

public interface AdicionarItemServicoOrcamentoUseCase {
    Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command);
}
