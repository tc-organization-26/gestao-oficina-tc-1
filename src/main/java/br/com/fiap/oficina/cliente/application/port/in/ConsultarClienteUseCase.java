package br.com.fiap.oficina.cliente.application.port.in;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;

public interface ConsultarClienteUseCase {

    Cliente consultarPorId(ClienteId clienteId);
}