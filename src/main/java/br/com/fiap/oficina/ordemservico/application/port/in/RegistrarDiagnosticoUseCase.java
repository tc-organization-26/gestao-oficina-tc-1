package br.com.fiap.oficina.ordemservico.application.port.in;

import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;

public interface RegistrarDiagnosticoUseCase {
    OrdemServico registrarDiagnostico(RegistrarDiagnosticoCommand command);
}
