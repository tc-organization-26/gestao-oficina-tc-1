package br.com.fiap.oficina.cliente.application.gateways;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.cliente.domain.entities.Cliente;
import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.cliente.domain.valueobjects.CpfCnpj;

public interface ClienteGateway {

    boolean existePorCpfCnpj(CpfCnpj cpfCnpj);

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(ClienteId clienteId);

    Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj);

    List<Cliente> buscarTodos();

    void excluirPorId(ClienteId clienteId);
}