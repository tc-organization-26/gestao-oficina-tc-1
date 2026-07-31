package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;

public interface AdicionarItemPecaOrcamentoUseCase {
    Orcamento adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command);
}