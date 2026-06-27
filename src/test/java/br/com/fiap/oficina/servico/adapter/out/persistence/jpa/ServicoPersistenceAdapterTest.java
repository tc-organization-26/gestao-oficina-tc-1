package br.com.fiap.oficina.servico.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServicoPersistenceAdapterTest {
    @Test
    void verificaExistenciaPorCodigo() {
        var repository = mock(SpringDataServicoRepository.class);
        when(repository.existsByCodigo("TROCA")).thenReturn(true);
        var adapter = new ServicoPersistenceAdapter(repository);

        assertTrue(adapter.existePorCodigo("TROCA"));
    }

    @Test
    void salvaConvertendoDominioParaEntity() {
        var repository = mock(SpringDataServicoRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var adapter = new ServicoPersistenceAdapter(repository);
        var servico = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);

        var salvo = adapter.salvar(servico);

        assertEquals(servico.id(), salvo.id());
        verify(repository).save(any(ServicoJpaEntity.class));
    }

    @Test
    void buscaPorIdConverteEntityParaDominio() {
        var id = UUID.randomUUID();
        var repository = mock(SpringDataServicoRepository.class);
        var entity = new ServicoJpaEntity(id, "TROCA", "Troca", BigDecimal.TEN, 60, true, OffsetDateTime.now(), OffsetDateTime.now());
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        var adapter = new ServicoPersistenceAdapter(repository);

        var servico = adapter.buscarPorId(new ServicoId(id));

        assertTrue(servico.isPresent());
        assertEquals("TROCA", servico.orElseThrow().codigo());
    }
}