package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public class OrdemServicoJpaMapper {
    public OrdemServicoJpaEntity toEntity(OrdemServico ordemServico) {
        return new OrdemServicoJpaEntity(
                ordemServico.id().value(),
                ordemServico.numero(),
                ordemServico.clienteId().value(),
                ordemServico.veiculoId().value(),
                ordemServico.status().ordinal(),
                ordemServico.anotacoes(),
                ordemServico.dataRecebimento(),
                ordemServico.inicioExecucaoEm(),
                ordemServico.finalizadaEm(),
                ordemServico.entregueEm());
    }

    public OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        return new OrdemServico(
                new OrdemServicoId(entity.getId()),
                entity.getNumero(),
                new ClienteId(entity.getClienteId()),
                new VeiculoId(entity.getVeiculoId()),
                StatusOrdemServico.values()[entity.getStatusOrdemServico()],
                entity.getAnotacoes(),
                entity.getDataRecebimento(),
                entity.getInicioExecucaoEm(),
                entity.getFinalizadaEm(),
                entity.getEntregueEm());
    }
}