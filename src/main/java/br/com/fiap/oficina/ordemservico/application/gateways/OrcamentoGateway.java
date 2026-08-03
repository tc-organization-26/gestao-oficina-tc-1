package br.com.fiap.oficina.ordemservico.application.gateways;

import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;

import java.util.Optional;

public interface OrcamentoGateway {
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId);
    Optional<Orcamento> buscarPorOrdemServicoId(OrdemServicoId ordemServicoId);
}