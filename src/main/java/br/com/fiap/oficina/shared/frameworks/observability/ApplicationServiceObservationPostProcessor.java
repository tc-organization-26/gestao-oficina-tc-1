package br.com.fiap.oficina.shared.frameworks.observability;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/** Composes observability outside the application using the project's service package convention. */
@Component
public class ApplicationServiceObservationPostProcessor implements BeanPostProcessor {
    private final ObjectProvider<ApplicationServiceFailureObserver> observers;

    public ApplicationServiceObservationPostProcessor(ObjectProvider<ApplicationServiceFailureObserver> observers) {
        this.observers = observers;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        var type = AopUtils.getTargetClass(bean);
        if (!type.getPackageName().endsWith(".application.usecases.interactors")) return bean;
        var proxy = new ProxyFactory(bean);
        proxy.setProxyTargetClass(true);
        proxy.addAdvice((MethodInterceptor) invocation -> {
            try {
                return invocation.proceed();
            } catch (Throwable failure) {
                for (var observer : observers.orderedStream().toList()) {
                    try {
                        observer.onFailure(type, invocation.getMethod(), failure);
                    } catch (RuntimeException telemetryFailure) {
                        LoggerFactory.getLogger(ApplicationServiceObservationPostProcessor.class)
                                .warn("Falha ao registrar observabilidade do caso de uso");
                    }
                }
                throw failure;
            }
        });
        return proxy.getProxy(type.getClassLoader());
    }
}
