package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa;

import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;

import java.util.List;

public class EstoqueVerificadorAdapter implements VerificadorEstoqueGateway {

    private final EstoqueGateway estoqueGateway;

    public EstoqueVerificadorAdapter(EstoqueGateway estoqueGateway) {
        this.estoqueGateway = estoqueGateway;
    }

    @Override
    public boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca) {
        if (itensPeca == null || itensPeca.isEmpty()) {
            return true; // Se não há peças, não há problema de estoque
        }

        for (ItemPeca peca : itensPeca) {
            var itemEstoque = estoqueGateway.buscarPorId(peca.itemEstoqueId());
            
            if (itemEstoque.isEmpty() || itemEstoque.get().quantidadeDisponivel().compareTo(peca.quantidade()) < 0) {
                return false; // Item não existe ou quantidade insuficiente
            }
        }

        return true; // Todos os itens estão disponíveis
    }
}
