package br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.application.port.out.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;

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
        var entity = new VeiculoJpaEntity(
                veiculo.id().value(),
                veiculo.clienteId().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano(),
                veiculo.criadoEm(),
                veiculo.atualizadoEm());

        var entitySalva = repository.save(entity);

        return toDomain(entitySalva);
    }

    @Override
    public Optional<Veiculo> buscarPorId(VeiculoId veiculoId) {
        return repository.findById(veiculoId.value())
                .map(this::toDomain);
    }

    @Override
    public List<Veiculo> buscarTodos() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void excluirPorId(VeiculoId veiculoId) {
        repository.deleteById(veiculoId.value());
    }
    
    @Override
    public List<Veiculo> buscarPorClienteId(ClienteId clienteId) {
        return repository.findByClienteId(clienteId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    private Veiculo toDomain(VeiculoJpaEntity entity) {
        return new Veiculo(
                new VeiculoId(entity.getId()),
                new ClienteId(entity.getClienteId()),
                VeiculoPlaca.novo(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }
}