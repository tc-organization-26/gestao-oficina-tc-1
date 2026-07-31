package br.com.fiap.oficina.ordemservico.application.usecases;

import br.com.fiap.oficina.ordemservico.application.dtos.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;

public interface RegistrarDiagnosticoUseCase {
    OrdemServico registrarDiagnostico(RegistrarDiagnosticoCommand command);
}
