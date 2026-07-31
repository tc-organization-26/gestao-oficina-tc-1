package br.com.fiap.oficina.servico.application.usecases;

import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;

public interface ConsultarServicoUseCase {
    Servico consultarPorId(ServicoId servicoId);
}
