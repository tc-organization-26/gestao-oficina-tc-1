package br.com.fiap.oficina.servico.application.port.in;

import br.com.fiap.oficina.servico.application.command.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.domain.model.Servico;

public interface AtualizarServicoUseCase {

    Servico atualizar(AtualizarServicoCommand command);
}