package br.com.fiap.oficina.servico.application.port.in;

import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;

public interface ConsultarServicoUseCase {
    Servico consultarPorId(ServicoId servicoId);
}
