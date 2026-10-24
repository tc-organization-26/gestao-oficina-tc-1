package br.com.fiap.oficina.shared.frameworks.observability;

import java.lang.reflect.Method;

/** Infrastructure extension point; use cases never depend on this observer. */
public interface ApplicationServiceFailureObserver {
    void onFailure(Class<?> serviceType, Method operation, Throwable failure);
}
