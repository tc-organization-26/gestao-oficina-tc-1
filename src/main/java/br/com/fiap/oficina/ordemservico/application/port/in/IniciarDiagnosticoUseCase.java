package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;

public interface IniciarDiagnosticoUseCase {
    OrdemServico iniciarDiagnostico(OrdemServicoId ordemServicoId);
}
