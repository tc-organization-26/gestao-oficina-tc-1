package br.com.fiap.oficina.servico.application.port.in;

import br.com.fiap.oficina.servico.domain.model.ServicoId;

public interface ExcluirServicoUseCase {
    void excluir(ServicoId servicoId);
}