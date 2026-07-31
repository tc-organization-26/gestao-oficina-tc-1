package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;

public interface IniciarDiagnosticoUseCase {
    OrdemServico iniciarDiagnostico(OrdemServicoId ordemServicoId);
}
