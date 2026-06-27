package br.com.fiap.oficina.cliente.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClientePersistenceAdapterTest {
    @Test
    void verificaExistenciaPorCpfCnpj() {
        var repository = mock(SpringDataClienteRepository.class);
        when(repository.existsByCpfCnpj("12345678901")).thenReturn(true);
        var adapter = new ClientePersistenceAdapter(repository);

        assertTrue(adapter.existePorCpfCnpj(CpfCnpj.novo("12345678901")));
    }

    @Test
    void salvaConvertendoDominioParaEntity() {
        var repository = mock(SpringDataClienteRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var adapter = new ClientePersistenceAdapter(repository);
        var cliente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11");

        var salvo = adapter.salvar(cliente);

        assertEquals(cliente.id(), salvo.id());
        verify(repository).save(any(ClienteJpaEntity.class));
    }

    @Test
    void buscaPorIdConverteEntityParaDominio() {
        var id = UUID.randomUUID();
        var repository = mock(SpringDataClienteRepository.class);
        var entity = new ClienteJpaEntity(id, "Maria", "12345678901", "11", "maria@email.com", true, OffsetDateTime.now(), OffsetDateTime.now());
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        var adapter = new ClientePersistenceAdapter(repository);

        var cliente = adapter.buscarPorId(new ClienteId(id));

        assertTrue(cliente.isPresent());
        assertEquals("Maria", cliente.orElseThrow().nome());
    }
}