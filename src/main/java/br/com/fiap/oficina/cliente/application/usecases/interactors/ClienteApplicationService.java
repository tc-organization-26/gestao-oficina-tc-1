package br.com.fiap.oficina.cliente.application.usecases.interactors;

import br.com.fiap.oficina.cliente.application.dtos.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.application.dtos.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.application.usecases.AtualizarClienteUseCase;
import br.com.fiap.oficina.cliente.application.usecases.CadastrarClienteUseCase;
import br.com.fiap.oficina.cliente.application.usecases.ConsultarClienteUseCase;
import br.com.fiap.oficina.cliente.application.usecases.ConsultarTodosClientesUseCase;
import br.com.fiap.oficina.cliente.application.usecases.ExcluirClienteUseCase;
import br.com.fiap.oficina.cliente.application.gateways.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.domain.entities.Cliente;
import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.cliente.domain.valueobjects.CpfCnpj;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

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