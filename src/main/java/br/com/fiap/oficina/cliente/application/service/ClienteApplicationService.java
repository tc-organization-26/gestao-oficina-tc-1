package br.com.fiap.oficina.cliente.application.service;

import br.com.fiap.oficina.cliente.application.command.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.application.command.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.application.port.in.AtualizarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.CadastrarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarTodosClientesUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ExcluirClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.out.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;
import br.com.fiap.oficina.shared.domain.DomainException;

import java.util.List;

public class ClienteApplicationService implements CadastrarClienteUseCase,
        AtualizarClienteUseCase,
        ConsultarClienteUseCase,
        ConsultarTodosClientesUseCase,
        ExcluirClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ClienteApplicationService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente cadastrar(CadastrarClienteCommand command) {
        var cpfCnpj = new CpfCnpj(command.cpfCnpj());

        if (clienteRepository.existePorCpfCnpj(cpfCnpj)) {
            throw new DomainException("CPF/CNPJ já cadastrado.");
        }

        var cliente = Cliente.criar(
                new CpfCnpj(command.cpfCnpj()),
                command.nome(),
                command.email(),
                command.telefone());

        return clienteRepository.salvar(cliente);
    }

    @Override
    public Cliente atualizar(AtualizarClienteCommand command) {
        var clienteId = new ClienteId(command.clienteId());

        var cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new DomainException("Cliente não encontrado."));

        cliente.atualizar(
                command.nome(),
                command.email(),
                command.telefone());

        return clienteRepository.salvar(cliente);
    }

    @Override
    public Cliente consultarPorId(ClienteId clienteId) {
        return clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new DomainException("Cliente não encontrado."));
    }

    @Override
    public Cliente consultarPorDocumento(String cpfCnpj) {
        return clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new DomainException("Cliente não encontrado."));
    }

    @Override
    public List<Cliente> consultarTodos() {
        return clienteRepository.buscarTodos();
    }

    @Override
    public void excluir(ClienteId clienteId) {
        consultarPorId(clienteId);
        clienteRepository.excluirPorId(clienteId);
    }
}