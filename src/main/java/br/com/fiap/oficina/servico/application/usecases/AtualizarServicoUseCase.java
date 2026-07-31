package br.com.fiap.oficina.servico.application.usecases;

import br.com.fiap.oficina.servico.application.dtos.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.domain.entities.Servico;

public interface AtualizarServicoUseCase {

    Servico atualizar(AtualizarServicoCommand command);
}