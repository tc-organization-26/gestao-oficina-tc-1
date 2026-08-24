package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;

public final class ItemServico {

    private final ServicoId servicoId;
    private final BigDecimal quantidade;

    public ItemServico(ServicoId servicoId, BigDecimal quantidade) {
        if (servicoId == null) throw new DomainException("ServicoId obrigatorio");
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Quantidade deve ser maior que zero");
        }
        this.servicoId = servicoId;
        this.quantidade = quantidade;
    }

    public ServicoId servicoId() { return servicoId; }
    public BigDecimal quantidade() { return quantidade; }
}
