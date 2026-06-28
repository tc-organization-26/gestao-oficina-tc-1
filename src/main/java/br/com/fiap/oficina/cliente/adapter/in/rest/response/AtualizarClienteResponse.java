package br.com.fiap.oficina.cliente.adapter.in.rest.response;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import java.util.UUID;

public record AtualizarClienteResponse(
        UUID id,
        String nome,
        String cpfCnpj
) {
    public static AtualizarClienteResponse from(Cliente cliente) {
        return new AtualizarClienteResponse(
                cliente.id().value(),
                cliente.nome(),
                cliente.cpfCnpj().value()
        );
    }
}
