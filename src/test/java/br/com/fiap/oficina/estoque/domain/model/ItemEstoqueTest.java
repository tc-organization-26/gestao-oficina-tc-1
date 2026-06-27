package br.com.fiap.oficina.estoque.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.shared.domain.DomainException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ItemEstoqueTest {
    @Test
    void criarNormalizaCodigoEDefineAtivo() {
        var item = ItemEstoque.criar("  oleo-01 ", "Oleo", BigDecimal.TEN, BigDecimal.ONE);

        assertEquals("OLEO-01", item.codigo());
        assertTrue(item.ativo());
        assertNotNull(item.criadoEm());
    }

    @Test
    void atualizarAlteraDescricaoValorEData() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);
        var atualizadoEm = item.atualizadoEm();

        item.atualizar("Filtro", BigDecimal.valueOf(20));

        assertEquals("Filtro", item.descricao());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(item.valorUnitario()));
        assertTrue(item.atualizadoEm().isAfter(atualizadoEm) || item.atualizadoEm().isEqual(atualizadoEm));
    }

    @Test
    void incluirSomaQuantidadeDisponivel() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);

        item.incluir(BigDecimal.valueOf(2));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(item.quantidadeDisponivel()));
    }

    @Test
    void baixarSubtraiQuantidadeDisponivel() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.valueOf(5));

        item.baixar(BigDecimal.valueOf(2));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(item.quantidadeDisponivel()));
    }

    @Test
    void baixarRejeitaQuantidadeIndisponivel() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);

        assertThrows(DomainException.class, () -> item.baixar(BigDecimal.TEN));
    }

    @Test
    void desativarMarcaItemComoInativo() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);

        item.desativar();

        assertFalse(item.ativo());
    }
}