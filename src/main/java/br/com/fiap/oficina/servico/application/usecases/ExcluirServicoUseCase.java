package br.com.fiap.oficina.servico.application.usecases;

import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;

public interface ExcluirServicoUseCase {
    void excluir(ServicoId servicoId);
}