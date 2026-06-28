package br.com.fiap.oficina.ordemservico.application.port.out;

import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;

import java.util.Optional;

public interface OrcamentoRepositoryPort {
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId);
}
