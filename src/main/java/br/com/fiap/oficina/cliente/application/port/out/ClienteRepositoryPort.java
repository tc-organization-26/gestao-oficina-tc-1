package br.com.fiap.oficina.cliente.application.port.out;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;

public interface ClienteRepositoryPort {

    boolean existePorCpfCnpj(CpfCnpj cpfCnpj);

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(ClienteId clienteId);

    List<Cliente> buscarTodos();

    void excluirPorId(ClienteId clienteId);
}