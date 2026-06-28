package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;

public interface AdicionarItemServicoOrcamentoUseCase {
    Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command);
}
