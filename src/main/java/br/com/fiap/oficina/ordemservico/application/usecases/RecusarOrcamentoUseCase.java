package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

import java.util.UUID;

public interface RecusarOrcamentoUseCase {
    OrdemServico recusar(UUID ordemId);
}
