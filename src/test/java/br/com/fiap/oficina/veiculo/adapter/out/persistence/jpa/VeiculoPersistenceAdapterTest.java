package br.com.fiap.oficina.veiculo.adapter.out.persistence.jpa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VeiculoPersistenceAdapterTest {
    @Test
    void verificaExistenciaPorPlaca() {
        var repository = mock(SpringDataVeiculoRepository.class);
        when(repository.existsByPlaca("ABC1D23")).thenReturn(true);
        var adapter = new VeiculoPersistenceAdapter(repository);

        assertTrue(adapter.existePorPlaca("ABC1D23"));
    }

    @Test
    void salvaConvertendoDominioParaEntity() {
        var repository = mock(SpringDataVeiculoRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var adapter = new VeiculoPersistenceAdapter(repository);
        var veiculo = Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);

        var salvo = adapter.salvar(veiculo);

        assertEquals(veiculo.id(), salvo.id());
        verify(repository).save(any(VeiculoJpaEntity.class));
    }

    @Test
    void buscaPorIdConverteEntityParaDominio() {
        var id = UUID.randomUUID();
        var repository = mock(SpringDataVeiculoRepository.class);
        var entity = new VeiculoJpaEntity(id, UUID.randomUUID(), "ABC1D23", "Toyota", "Corolla", 2020, OffsetDateTime.now(), OffsetDateTime.now());
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        var adapter = new VeiculoPersistenceAdapter(repository);

        var veiculo = adapter.buscarPorId(new VeiculoId(id));

        assertTrue(veiculo.isPresent());
        assertEquals("Toyota", veiculo.orElseThrow().marca());
    }
}