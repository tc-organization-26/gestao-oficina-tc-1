package br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;

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
