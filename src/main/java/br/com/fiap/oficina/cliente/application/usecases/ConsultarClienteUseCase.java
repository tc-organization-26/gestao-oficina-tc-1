package br.com.fiap.oficina.cliente.application.usecases;

import br.com.fiap.oficina.cliente.domain.entities.Cliente;
import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;

public interface ConsultarClienteUseCase {

    Cliente consultarPorId(ClienteId clienteId);

    Cliente consultarPorDocumento(String cpfCnpj);
}