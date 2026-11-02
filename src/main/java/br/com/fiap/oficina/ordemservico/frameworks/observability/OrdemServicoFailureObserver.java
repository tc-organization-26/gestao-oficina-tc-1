package br.com.fiap.oficina.ordemservico.frameworks.observability;

import br.com.fiap.oficina.ordemservico.application.usecases.interactors.OrdemServicoApplicationService;
import br.com.fiap.oficina.ordemservico.application.usecases.interactors.OrcamentoApplicationService;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.shared.frameworks.observability.ApplicationServiceFailureObserver;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Component
public class OrdemServicoFailureObserver implements ApplicationServiceFailureObserver {
    @Override
    public void onFailure(Class<?> serviceType, Method operation, Throwable failure) {
        if (!OrdemServicoApplicationService.class.isAssignableFrom(serviceType)
                && !OrcamentoApplicationService.class.isAssignableFrom(serviceType)) return;
        if (failure instanceof DomainException) return;
        LoggerFactory.getLogger(OrdemServicoFailureObserver.class).atError()
                .addKeyValue("event", "os.processing_failed").addKeyValue("operacao", operation.getName())
                .addKeyValue("error_type", failure.getClass().getName())
                .log("Falha no processamento de ordem de servico");
    }
}
