package br.com.fiap.oficina.veiculo.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.veiculo.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoPlaca;

public class VeiculoMapper {
    public static Veiculo toDomain(VeiculoJpaEntity entity) {
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

    public static VeiculoJpaEntity toEntity(Veiculo veiculo) {
        return new VeiculoJpaEntity(
                veiculo.id().value(),
                veiculo.clienteId().value(),
                veiculo.placa().value(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano(),
                veiculo.criadoEm(),
                veiculo.atualizadoEm());
    }
}
