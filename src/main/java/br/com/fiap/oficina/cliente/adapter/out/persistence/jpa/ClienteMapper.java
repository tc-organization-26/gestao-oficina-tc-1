package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;

public class ClienteMapper {
    public static Cliente toDomain(ClienteJpaEntity entity) {
        return new Cliente(
                new ClienteId(entity.getId()),
                new CpfCnpj(entity.getCpfCnpj()),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getAtivo(),
                entity.getAtualizadoEm(),
                entity.getCriadoEm());
    }

    public static ClienteJpaEntity toEntity(Cliente cliente) {
        return new ClienteJpaEntity(
                cliente.id().value(),
                cliente.nome(),
                cliente.cpfCnpj().value(),
                cliente.telefone(),
                cliente.email(),
                cliente.ativo(),
                cliente.criadoEm(),
                cliente.atualizadoEm());
    }
}
