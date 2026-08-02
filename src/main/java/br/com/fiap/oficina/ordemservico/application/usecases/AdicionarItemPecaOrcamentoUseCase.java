package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface AdicionarItemPecaOrcamentoUseCase {
    OrdemServico adicionarItemPeca(AdicionarItemPecaOrcamentoCommand command);
}