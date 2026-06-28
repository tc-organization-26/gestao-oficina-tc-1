package br.com.fiap.oficina.cliente.adapter.in.rest.request;

import java.util.UUID;

public record ConsultarVeiculosDoClienteRequest(
        UUID clienteId
) {
}
