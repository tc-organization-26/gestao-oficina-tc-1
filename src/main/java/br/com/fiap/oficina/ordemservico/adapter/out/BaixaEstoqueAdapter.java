package br.com.fiap.oficina.ordemservico.adapter.out;

import br.com.fiap.oficina.estoque.application.command.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.BaixaEstoquePort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class BaixaEstoqueAdapter implements BaixaEstoquePort {

    private final BaixarItemEstoqueUseCase baixarItemEstoqueUseCase;

    public BaixaEstoqueAdapter(BaixarItemEstoqueUseCase baixarItemEstoqueUseCase) {
        this.baixarItemEstoqueUseCase = baixarItemEstoqueUseCase;
    }

    @Override
    public void baixar(UUID itemEstoqueId, BigDecimal quantidade) {
        baixarItemEstoqueUseCase.baixar(new BaixarItemEstoqueCommand(itemEstoqueId, quantidade));
    }
}
