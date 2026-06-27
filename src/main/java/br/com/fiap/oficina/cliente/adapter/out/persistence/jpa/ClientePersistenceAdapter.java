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
        var entity = new ClienteJpaEntity(
                cliente.id().value(),
                cliente.nome(),
                cliente.cpfCnpj().value(),
                cliente.telefone(),
                cliente.email(),
                cliente.ativo(),
                cliente.criadoEm(),
                cliente.atualizadoEm());

        var entitySalva = repository.save(entity);

        return toDomain(entitySalva);
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId clienteId) {
        return repository.findById(clienteId.value())
                .map(this::toDomain);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(ClienteId clienteId) {
        repository.deleteById(clienteId.value());
    }

    private Cliente toDomain(ClienteJpaEntity entitySalva) {
        return new Cliente(
                new ClienteId(entitySalva.getId()),
                new CpfCnpj(entitySalva.getCpfCnpj()),
                entitySalva.getNome(),
                entitySalva.getEmail(),
                entitySalva.getTelefone(),
                entitySalva.getAtivo(),
                entitySalva.getAtualizadoEm(),
                entitySalva.getCriadoEm());
    }
}