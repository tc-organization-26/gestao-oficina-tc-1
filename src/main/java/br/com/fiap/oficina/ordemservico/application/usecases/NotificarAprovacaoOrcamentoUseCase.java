package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.NotificarAprovacaoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface NotificarAprovacaoOrcamentoUseCase {
    OrdemServico notificarAprovacao(NotificarAprovacaoOrcamentoCommand command);
}
