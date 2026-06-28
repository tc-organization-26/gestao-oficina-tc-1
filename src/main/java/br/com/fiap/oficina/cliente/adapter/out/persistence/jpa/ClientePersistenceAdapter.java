package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.cliente.application.port.out.ClienteRepositoryPort;
import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;

public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository repository;

    public ClientePersistenceAdapter(SpringDataClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) {
        return repository.existsByCpfCnpj(cpfCnpj.value());
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        var entity = ClienteMapper.toEntity(cliente);
        var entitySalva = repository.save(entity);
        return ClienteMapper.toDomain(entitySalva);
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId clienteId) {
        return repository.findById(clienteId.value())
                .map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return repository.findAll().stream()
                .map(ClienteMapper::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(ClienteId clienteId) {
        repository.deleteById(clienteId.value());
    }
}