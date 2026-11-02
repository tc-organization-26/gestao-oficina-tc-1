package br.com.fiap.oficina.shared.frameworks.observability;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.sql.SQLException;

@Component
public class PostgresqlFailureObserver implements ApplicationServiceFailureObserver {
    @Override
    public void onFailure(Class<?> serviceType, Method operation, Throwable failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof DataAccessException || cause instanceof SQLException) {
                LoggerFactory.getLogger(PostgresqlFailureObserver.class).atError()
                        .addKeyValue("event", "integration.failed").addKeyValue("integracao", "postgresql")
                        .addKeyValue("operacao", operation.getName()).addKeyValue("error_type", cause.getClass().getName())
                        .log("Falha na integracao com banco de dados");
                break;
            }
        }
    }
}
