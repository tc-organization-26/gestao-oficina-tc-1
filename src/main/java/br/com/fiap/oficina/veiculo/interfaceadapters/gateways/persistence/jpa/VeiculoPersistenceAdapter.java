package br.com.fiap.oficina.veiculo.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.veiculo.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.veiculo.application.gateways.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoPlaca;

import java.util.List;
import java.util.Optional;

public class VeiculoPersistenceAdapter implements VeiculoRepositoryPort {

    private final SpringDataVeiculoRepository repository;

    public VeiculoPersistenceAdapter(SpringDataVeiculoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorPlaca(String placa) {
        return repository.existsByPlaca(placa);
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        var entity = VeiculoMapper.toEntity(veiculo);
        var entitySalva = repository.save(entity);
        return VeiculoMapper.toDomain(entitySalva);
    }

    @Override
    public Optional<Veiculo> buscarPorId(VeiculoId veiculoId) {
        return repository.findById(veiculoId.value())
                .map(VeiculoMapper::toDomain);
    }

    @Override
    public List<Veiculo> buscarTodos() {
        return repository.findAll().stream()
                .map(VeiculoMapper::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(VeiculoId veiculoId) {
        repository.deleteById(veiculoId.value());
    }
    
    @Override
    public List<Veiculo> buscarPorClienteId(ClienteId clienteId) {
        return repository.findByClienteId(clienteId.value()).stream()
                .map(VeiculoMapper::toDomain)
                .toList();
    }
}