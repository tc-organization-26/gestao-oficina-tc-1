package br.com.fiap.oficina.servico.application.usecases;

import br.com.fiap.oficina.servico.application.dtos.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.domain.entities.Servico;

public interface CadastrarServicoUseCase {
    Servico cadastrar(CadastrarServicoCommand command);
}
