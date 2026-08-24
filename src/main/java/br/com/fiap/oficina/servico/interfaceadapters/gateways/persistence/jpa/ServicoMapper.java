package br.com.fiap.oficina.servico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.servico.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;

public class ServicoMapper {
    public static Servico toDomain(ServicoJpaEntity entity) {
        return new Servico(
                new ServicoId(entity.getId()),
                entity.getCodigo(),
                entity.getDescricao(),
                entity.getValorUnitario(),
                entity.getTempoEstimadoMinutos(),
                entity.isAtivo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }

    public static ServicoJpaEntity toEntity(Servico servico) {
        return new ServicoJpaEntity(
                servico.id().value(),
                servico.codigo(),
                servico.descricao(),
                servico.valorUnitario(),
                servico.tempoEstimadoMinutos(),
                servico.ativo(),
                servico.criadoEm(),
                servico.atualizadoEm());
    }
}
