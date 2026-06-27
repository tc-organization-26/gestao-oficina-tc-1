package br.com.fiap.oficina.estoque.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EstoquePersistenceAdapterTest {
    @Test
    void verificaExistenciaPorCodigo() {
        var repository = mock(SpringDataEstoqueRepository.class);
        when(repository.existsByCodigo("OLEO")).thenReturn(true);
        var adapter = new EstoquePersistenceAdapter(repository);

        assertTrue(adapter.existePorCodigo("OLEO"));
    }

    @Test
    void salvaConvertendoDominioParaEntity() {
        var repository = mock(SpringDataEstoqueRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var adapter = new EstoquePersistenceAdapter(repository);
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);

        var salvo = adapter.salvar(item);

        assertEquals(item.id(), salvo.id());
        verify(repository).save(any(ItemEstoqueJpaEntity.class));
    }

    @Test
    void buscaPorIdConverteEntityParaDominio() {
        var id = UUID.randomUUID();
        var repository = mock(SpringDataEstoqueRepository.class);
        var entity = entity(id);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        var adapter = new EstoquePersistenceAdapter(repository);

        var item = adapter.buscarPorId(new ItemEstoqueId(id));

        assertTrue(item.isPresent());
        assertEquals("OLEO", item.orElseThrow().codigo());
    }

    @Test
    void buscarTodosConverteEntitiesParaDominio() {
        var repository = mock(SpringDataEstoqueRepository.class);
        when(repository.findAll()).thenReturn(List.of(entity(UUID.randomUUID())));
        var adapter = new EstoquePersistenceAdapter(repository);

        var itens = adapter.buscarTodos();

        assertEquals(1, itens.size());
        assertEquals("OLEO", itens.get(0).codigo());
    }

    private static ItemEstoqueJpaEntity entity(UUID id) {
        var agora = OffsetDateTime.now();
        return new ItemEstoqueJpaEntity(id, "OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE, true, agora, agora);
    }
}