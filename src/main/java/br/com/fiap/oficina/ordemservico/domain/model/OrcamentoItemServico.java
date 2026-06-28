package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.servico.domain.model.ServicoId;

public final class OrcamentoItemServico {

    private final ServicoId servicoId;
    private final double quantidade;

    public OrcamentoItemServico(ServicoId servicoId, double quantidade) {
        if (servicoId == null) throw new DomainException("ServicoId obrigatorio");
        if (quantidade <= 0) throw new DomainException("Quantidade deve ser maior que zero");
        this.servicoId = servicoId;
        this.quantidade = quantidade;
    }

    public ServicoId servicoId() { return servicoId; }
    public double quantidade() { return quantidade; }
}
