package br.com.fiap.oficina.ordemservico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
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
        var servico = new OrcamentoItemServico(new ServicoId(UUID.randomUUID()), 1.0);
        var peca = new ItemPeca(new ItemEstoqueId(UUID.randomUUID()), 2.0);

        orcamento.adicionarItemServico(servico);
        orcamento.adicionarItemPeca(peca);

        assertEquals(1, orcamento.itensServico().size());
        assertEquals(1, orcamento.itensPeca().size());
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
        assertEquals(StatusOrcamento.FINALIZADO, orcamento.status());
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
