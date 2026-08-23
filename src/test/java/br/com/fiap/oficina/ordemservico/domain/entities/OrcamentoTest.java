package br.com.fiap.oficina.ordemservico.domain.entities;

import br.com.fiap.oficina.ordemservico.domain.enums.*;

import br.com.fiap.oficina.ordemservico.domain.valueobjects.*;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoTest {

    @Test
    void novoCriaOrcamentoAbertoParaOrdem() {
        var ordemId = OrdemServicoId.novo();

        var orcamento = Orcamento.novo(ordemId);

        assertNotNull(orcamento.id());
        assertEquals(ordemId, orcamento.ordemServicoId());
        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertNull(orcamento.dataFechamento());
    }

    @Test
    void rejeitaOrdemServicoNula() {
        assertThrows(DomainException.class, () -> new Orcamento(UUID.randomUUID(), null));
    }

    @Test
    void adicionaItensServicoEPeca() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());
        var servico = new OrcamentoItemServico(new ServicoId(UUID.randomUUID()), java.math.BigDecimal.ONE);
        var peca = new ItemPeca(new ItemEstoqueId(UUID.randomUUID()), java.math.BigDecimal.valueOf(2));

        orcamento.adicionarItemServico(servico);
        orcamento.adicionarItemPeca(peca);

        assertEquals(1, orcamento.itensServico().size());
        assertEquals(1, orcamento.itensPeca().size());
    }

    @Test
    void somaQuantidadeQuandoServicoJaExisteNoOrcamento() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());
        var servicoId = new ServicoId(UUID.randomUUID());

        orcamento.adicionarItemServico(new OrcamentoItemServico(servicoId, java.math.BigDecimal.ONE));
        orcamento.adicionarItemServico(new OrcamentoItemServico(servicoId, java.math.BigDecimal.valueOf(2)));

        assertEquals(1, orcamento.itensServico().size());
        assertEquals(java.math.BigDecimal.valueOf(3), orcamento.itensServico().get(0).quantidade());
    }

    @Test
    void somaQuantidadeQuandoPecaJaExisteNoOrcamento() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());
        var itemEstoqueId = new ItemEstoqueId(UUID.randomUUID());

        orcamento.adicionarItemPeca(new ItemPeca(itemEstoqueId, java.math.BigDecimal.ONE));
        orcamento.adicionarItemPeca(new ItemPeca(itemEstoqueId, java.math.BigDecimal.valueOf(2)));

        assertEquals(1, orcamento.itensPeca().size());
        assertEquals(java.math.BigDecimal.valueOf(3), orcamento.itensPeca().get(0).quantidade());
    }

    @Test
    void rejeitaItensNulos() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());

        assertThrows(DomainException.class, () -> orcamento.adicionarItemServico(null));
        assertThrows(DomainException.class, () -> orcamento.adicionarItemPeca(null));
    }

    @Test
    void controlaTransicoesDeStatus() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());

        orcamento.fechar();
        assertEquals(StatusOrcamento.ENVIADO, orcamento.status());
        assertNotNull(orcamento.dataFechamento());

        orcamento.aprovar();
        assertEquals(StatusOrcamento.APROVADO, orcamento.status());

        orcamento.reabrir();
        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertNull(orcamento.dataFechamento());
    }

    @Test
    void rejeitaTransicoesInvalidas() {
        var orcamento = Orcamento.novo(OrdemServicoId.novo());

        assertThrows(DomainException.class, orcamento::aprovar);
        assertThrows(DomainException.class, orcamento::recusar);

        orcamento.fechar();
        orcamento.aprovar();

        assertThrows(DomainException.class, orcamento::fechar);
    }
}
