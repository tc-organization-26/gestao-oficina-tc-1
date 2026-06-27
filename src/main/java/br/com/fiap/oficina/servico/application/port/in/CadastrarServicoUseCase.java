package br.com.fiap.oficina.servico.application.port.in;

import br.com.fiap.oficina.servico.application.command.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.domain.model.Servico;

public interface CadastrarServicoUseCase {
    Servico cadastrar(CadastrarServicoCommand command);
}
