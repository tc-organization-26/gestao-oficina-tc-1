package br.com.fiap.oficina.ordemservico.application.port.out;

import br.com.fiap.oficina.shared.domain.DomainEvent;

public interface PublicarEventoPort {
    void publicar(DomainEvent evento);
}
