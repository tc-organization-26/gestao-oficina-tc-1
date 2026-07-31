package br.com.fiap.oficina.servico.application.gateways;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;

public interface ServicoRepositoryPort {

    boolean existePorCodigo(String codigo);

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(ServicoId servicoId);

    Optional<Servico> buscarPorCodigo(String codigo);

    List<Servico> buscarTodos();

    void excluirPorId(ServicoId servicoId);
}