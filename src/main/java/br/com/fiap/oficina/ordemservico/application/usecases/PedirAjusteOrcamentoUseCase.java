package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

import java.util.UUID;

public interface PedirAjusteOrcamentoUseCase {
    OrdemServico pedirAjuste(UUID ordemId);
}
