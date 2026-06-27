package br.com.fiap.oficina.servico.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;

public interface ServicoRepositoryPort {

    boolean existePorCodigo(String codigo);

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(ServicoId servicoId);

    List<Servico> buscarTodos();

    void excluirPorId(ServicoId servicoId);
}