package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

public final class ItemServico {

    private final ServicoId servicoId;
    private final double quantidade;

    public ItemServico(ServicoId servicoId, double quantidade) {
        if (servicoId == null) throw new DomainException("ServicoId obrigatorio");
        if (quantidade <= 0) throw new DomainException("Quantidade deve ser maior que zero");
        this.servicoId = servicoId;
        this.quantidade = quantidade;
    }

    public ServicoId servicoId() { return servicoId; }
    public double quantidade() { return quantidade; }
}
