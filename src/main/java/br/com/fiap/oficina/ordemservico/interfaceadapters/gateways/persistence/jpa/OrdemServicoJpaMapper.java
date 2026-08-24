package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.ordemservico.domain.entities.Diagnostico;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;

public class OrdemServicoJpaMapper {
    public OrdemServicoJpaEntity toEntity(OrdemServico ordemServico) {
        var entity = new OrdemServicoJpaEntity(
                ordemServico.id().value(),
                ordemServico.numero(),
                ordemServico.clienteId().value(),
                ordemServico.veiculoId().value(),
                ordemServico.status().ordinal(),
                ordemServico.anotacoes(),
                ordemServico.dataRecebimento(),
                ordemServico.inicioExecucaoEm(),
                ordemServico.finalizadaEm(),
                ordemServico.entregueEm(),
                ordemServico.pago());

        if (ordemServico.diagnostico() != null) {
            var diagnostico = ordemServico.diagnostico();
            entity.setDiagnostico(new DiagnosticoJpaEntity(
                    diagnostico.id(),
                    entity,
                    diagnostico.descricao(),
                    diagnostico.criadoEm(),
                    diagnostico.atualizadoEm()));
        }

        

        return entity;
    }

    public OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        return toDomain(entity, null);
    }

    public OrdemServico toDomain(OrdemServicoJpaEntity entity, Orcamento orcamento) {
        return new OrdemServico(
                new OrdemServicoId(entity.getId()),
                entity.getNumero(),
                new ClienteId(entity.getClienteId()),
                new VeiculoId(entity.getVeiculoId()),
                StatusOrdemServico.values()[entity.getStatusOrdemServico()],
                entity.getAnotacoes(),
                toDomain(entity.getDiagnostico()),
                entity.getDataRecebimento(),
                entity.getInicioExecucaoEm(),
                entity.getFinalizadaEm(),
                entity.getEntregueEm(),
                entity.isPago(),
                orcamento);
    }



    private Diagnostico toDomain(DiagnosticoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Diagnostico(
                entity.getId(),
                entity.getDescricao(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm());
    }
}
