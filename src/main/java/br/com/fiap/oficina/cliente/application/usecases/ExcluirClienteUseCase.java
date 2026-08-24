package br.com.fiap.oficina.cliente.application.usecases;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;

public interface ExcluirClienteUseCase {
    void excluir(ClienteId clienteId);
}