package br.com.fiap.oficina.ordemservico.application.gateways;

import br.com.fiap.oficina.shared.domain.events.DomainEvent;

public interface PublicadorEventoGateway {
    void publicar(DomainEvent evento);
}
