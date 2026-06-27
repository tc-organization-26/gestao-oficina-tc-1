package br.com.fiap.oficina.cliente.adapter.in.rest.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import br.com.fiap.oficina.cliente.domain.model.Cliente;

public record ClienteResponse(
        UUID id,
        String cpfCnpj,
        String nome,
        String email,
        String telefone,
        boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.id().value(),
                cliente.cpfCnpj().value(),
                cliente.nome(),
                cliente.email(),
                cliente.telefone(),
                cliente.ativo(),
                cliente.criadoEm(),
                cliente.atualizadoEm());
    }
}
