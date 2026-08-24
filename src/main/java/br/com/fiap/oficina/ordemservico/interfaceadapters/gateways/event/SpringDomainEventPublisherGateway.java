package br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.event;

import br.com.fiap.oficina.ordemservico.application.gateways.PublicadorEventoGateway;
import br.com.fiap.oficina.shared.domain.events.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringDomainEventPublisherGateway implements PublicadorEventoGateway {

    private final ApplicationEventPublisher eventPublisher;

    public SpringDomainEventPublisherGateway(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publicar(DomainEvent evento) {
        eventPublisher.publishEvent(evento);
    }
}
