package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;

public interface FecharOrcamentoUseCase {
    void fechar(FecharOrcamentoCommand command);
}
