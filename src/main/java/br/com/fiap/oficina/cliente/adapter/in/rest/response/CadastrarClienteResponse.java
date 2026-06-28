package br.com.fiap.oficina.cliente.adapter.in.rest.response;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import java.util.UUID;

public record CadastrarClienteResponse(
        UUID id,
        String nome,
        String cpfCnpj
) {
    public static CadastrarClienteResponse from(Cliente cliente) {
        return new CadastrarClienteResponse(
                cliente.id().value(),
                cliente.nome(),
                cliente.cpfCnpj().value()
        );
    }
}
