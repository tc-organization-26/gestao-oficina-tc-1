package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;

public class ServicoPersistenceAdapter implements ServicoRepositoryPort {

    private final SpringDataServicoRepository repository;

    public ServicoPersistenceAdapter(SpringDataServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return repository.existsByCodigo(codigo);
    }

    @Override
    public Servico salvar(Servico servico) {
        var entity = ServicoMapper.toEntity(servico);
        var entitySalva = repository.save(entity);
        return ServicoMapper.toDomain(entitySalva);
    }

    @Override
    public Optional<Servico> buscarPorId(ServicoId servicoId) {
        return repository.findById(servicoId.value())
                .map(ServicoMapper::toDomain);
    }

    @Override
    public Optional<Servico> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .map(ServicoMapper::toDomain);
    }

    @Override
    public List<Servico> buscarTodos() {
        return repository.findAll().stream()
                .map(ServicoMapper::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(ServicoId servicoId) {
        repository.deleteById(servicoId.value());
    }
}