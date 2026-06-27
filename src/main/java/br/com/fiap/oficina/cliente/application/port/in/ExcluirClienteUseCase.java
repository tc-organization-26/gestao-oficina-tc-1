package br.com.fiap.oficina.cliente.application.port.in;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;

public interface ExcluirClienteUseCase {
    void excluir(ClienteId clienteId);
}