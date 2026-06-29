package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.VerificadorEstoquePort;
import br.com.fiap.oficina.ordemservico.domain.model.ItemPeca;

import java.util.List;

public class EstoqueVerificadorAdapter implements VerificadorEstoquePort {

    private final EstoqueRepositoryPort estoqueRepository;

    public EstoqueVerificadorAdapter(EstoqueRepositoryPort estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca) {
        if (itensPeca == null || itensPeca.isEmpty()) {
            return true; // Se não há peças, não há problema de estoque
        }

        for (ItemPeca peca : itensPeca) {
            var itemEstoque = estoqueRepository.buscarPorId(peca.itemEstoqueId());
            
            if (itemEstoque.isEmpty() || itemEstoque.get().quantidadeDisponivel().doubleValue() < peca.quantidade()) {
                return false; // Item não existe ou quantidade insuficiente
            }
        }

        return true; // Todos os itens estão disponíveis
    }
}
