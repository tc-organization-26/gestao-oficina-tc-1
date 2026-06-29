package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;

public interface AdicionarItemPecaOrcamentoUseCase {
    Orcamento adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command);
}