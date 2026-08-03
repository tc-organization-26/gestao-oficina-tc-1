package br.com.fiap.oficina.ordemservico.application.gateways;

import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import java.util.List;

public interface VerificadorEstoqueGateway {
    /**
     * Verifica se todos os itens de peça estão disponíveis no estoque.
     * @param itensPeca Lista de peças a verificar
     * @return true se todas as peças têm quantidade suficiente, false caso contrário
     */
    boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca);
}
