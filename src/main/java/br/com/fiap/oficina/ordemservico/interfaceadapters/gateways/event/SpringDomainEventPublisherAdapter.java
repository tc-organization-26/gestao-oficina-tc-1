package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.event;

import br.com.fiap.oficina.ordemservico.application.gateways.PublicarEventoPort;
import br.com.fiap.oficina.shared.domain.events.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringDomainEventPublisherAdapter implements PublicarEventoPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringDomainEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publicar(DomainEvent evento) {
        eventPublisher.publishEvent(evento);
    }
}
