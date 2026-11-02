package br.com.fiap.oficina.shared.frameworks.observability;

import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.usecases.interactors.OrdemServicoApplicationService;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.frameworks.observability.OrdemServicoFailureObserver;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import java.lang.reflect.Proxy;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationServiceObservationTest {
    private OrdemServicoApplicationService service(RuntimeException failure, boolean brokenObserver) {
        var gateway = (OrdemServicoGateway) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{OrdemServicoGateway.class}, (proxy, method, args) -> { throw failure; });
        var factory = new StaticListableBeanFactory();
        factory.addBean("os", new OrdemServicoFailureObserver());
        factory.addBean("database", new PostgresqlFailureObserver());
        if (brokenObserver) factory.addBean("broken", (ApplicationServiceFailureObserver) (type, method, error) -> {
            throw new IllegalStateException("Telemetry unavailable");
        });
        var postProcessor = new ApplicationServiceObservationPostProcessor(factory.getBeanProvider(ApplicationServiceFailureObserver.class));
        return (OrdemServicoApplicationService) postProcessor.postProcessAfterInitialization(
                new OrdemServicoApplicationService(gateway, null, null, null, null, null), "ordemServicoApplicationService");
    }

    @Test
    void reportsProcessingAndIntegrationFailureWithoutHttpAndPreservesException() {
        var osLogger = (Logger) LoggerFactory.getLogger(OrdemServicoFailureObserver.class);
        var databaseLogger = (Logger) LoggerFactory.getLogger(PostgresqlFailureObserver.class);
        var logs = new ListAppender<ILoggingEvent>();
        logs.start();
        osLogger.addAppender(logs);
        databaseLogger.addAppender(logs);
        try {
            var failure = new DataAccessResourceFailureException("Sensitive SQL");
            var proxy = service(failure, false);
            assertSame(failure, assertThrows(DataAccessResourceFailureException.class,
                    () -> proxy.consultarPorId(new OrdemServicoId(UUID.randomUUID()))));
            var fields = logs.list.stream().flatMap(e -> e.getKeyValuePairs().stream()).map(Object::toString).toList().toString();
            assertTrue(fields.contains("os.processing_failed"));
            assertTrue(fields.contains("integration.failed"));
            assertTrue(fields.contains("consultarPorId"));
            assertFalse(logs.list.toString().contains("Sensitive SQL"));
        } finally {
            osLogger.detachAppender(logs);
            databaseLogger.detachAppender(logs);
            logs.stop();
        }
    }

    @Test
    void doesNotReportBusinessRejectionAsProcessingFailure() {
        var logger = (Logger) LoggerFactory.getLogger(OrdemServicoFailureObserver.class);
        var logs = new ListAppender<ILoggingEvent>();
        logs.start();
        logger.addAppender(logs);
        try {
            var failure = new DomainException("Ordem inexistente");
            assertSame(failure, assertThrows(DomainException.class,
                    () -> service(failure, false).consultarPorId(new OrdemServicoId(UUID.randomUUID()))));
            assertTrue(logs.list.isEmpty());
        } finally { logger.detachAppender(logs); logs.stop(); }
    }

    @Test
    void telemetryFailureDoesNotReplaceOriginalException() {
        var failure = new IllegalStateException("Original failure");
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> service(failure, true).consultarPorId(new OrdemServicoId(UUID.randomUUID()))));
    }
}
