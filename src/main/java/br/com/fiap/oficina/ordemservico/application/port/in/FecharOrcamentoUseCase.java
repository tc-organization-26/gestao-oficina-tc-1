package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;

public interface FecharOrcamentoUseCase {
    void fechar(FecharOrcamentoCommand command);
}
