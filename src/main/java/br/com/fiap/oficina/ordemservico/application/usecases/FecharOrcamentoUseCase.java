package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface FecharOrcamentoUseCase {
    OrdemServico fechar(FecharOrcamentoCommand command);
}
