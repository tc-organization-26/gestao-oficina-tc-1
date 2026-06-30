package br.com.fiap.oficina.ordemservico.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrdemServicoTest {

    @Test
    void criarInicializaOrdemRecebidaComOrcamento() {
        var clienteId = new ClienteId(UUID.randomUUID());
        var veiculoId = new VeiculoId(UUID.randomUUID());

        var ordem = OrdemServico.criar(clienteId, veiculoId, "Barulho ao frear");

        assertNotNull(ordem.id());
        assertEquals(clienteId, ordem.clienteId());
        assertEquals(veiculoId, ordem.veiculoId());
        assertEquals(StatusOrdemServico.RECEBIDA, ordem.status());
        assertFalse(ordem.pago());
        assertNotNull(ordem.orcamento());
        assertEquals(ordem.id(), ordem.orcamento().ordemServicoId());
        assertNotEquals(ordem.id().value(), ordem.orcamento().id().value());
    }

    @Test
    void rejeitaCamposObrigatorios() {
        var clienteId = new ClienteId(UUID.randomUUID());
        var veiculoId = new VeiculoId(UUID.randomUUID());

        assertThrows(DomainException.class, () -> new OrdemServico(null, null, clienteId, veiculoId, null, null, null, null, null, null, null, false));
        assertThrows(DomainException.class, () -> new OrdemServico(OrdemServicoId.novo(), null, null, veiculoId, null, null, null, null, null, null, null, false));
        assertThrows(DomainException.class, () -> new OrdemServico(OrdemServicoId.novo(), null, clienteId, null, null, null, null, null, null, null, null, false));
    }

    @Test
    void percorreFluxoPrincipalAteEntrega() {
        var ordem = novaOrdem();

        ordem.iniciarDiagnostico();
        ordem.registrarDiagnostico(Diagnostico.registrar("Trocar pastilhas"));
        ordem.finalizarOrcamento();
        ordem.iniciarExecucao();
        ordem.finalizar();
        ordem.registrarPagamento();
        ordem.entregar();

        assertEquals(StatusOrdemServico.ENTREGUE, ordem.status());
        assertTrue(ordem.pago());
        assertNotNull(ordem.diagnostico());
        assertNotNull(ordem.inicioExecucaoEm());
        assertNotNull(ordem.finalizadaEm());
        assertNotNull(ordem.entregueEm());
    }

    @Test
    void rejeitaTransicoesForaDeOrdem() {
        var ordem = novaOrdem();

        assertThrows(DomainException.class, () -> ordem.registrarDiagnostico(Diagnostico.registrar("Diagnostico")));
        assertThrows(DomainException.class, ordem::iniciarExecucao);
        assertThrows(DomainException.class, ordem::finalizar);
        assertThrows(DomainException.class, ordem::registrarPagamento);
        assertThrows(DomainException.class, ordem::entregar);
    }

    @Test
    void exigePagamentoAntesDaEntrega() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        ordem.finalizarOrcamento();
        ordem.iniciarExecucao();
        ordem.finalizar();

        assertThrows(DomainException.class, ordem::entregar);
    }

    @Test
    void ajusteEAlteracaoDeOrcamentoRetornamParaEstadosEsperados() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        ordem.finalizarOrcamento();

        ordem.pedirAjuste();
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ordem.status());

        ordem.finalizarOrcamento();
        ordem.iniciarExecucao();
        ordem.pedirAjuste();
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ordem.status());
    }


    @Test
    void reconstruirSemOrcamentoNaoCriaOrcamentoNovo() {
        var ordem = new OrdemServico(
                OrdemServicoId.novo(),
                null,
                new ClienteId(UUID.randomUUID()),
                new VeiculoId(UUID.randomUUID()),
                StatusOrdemServico.RECEBIDA,
                "Revisao",
                null,
                null,
                null,
                null,
                null,
                false,
                null);

        assertNull(ordem.orcamento());
    }
    private static OrdemServico novaOrdem() {
        return OrdemServico.criar(new ClienteId(UUID.randomUUID()), new VeiculoId(UUID.randomUUID()), "Revisao");
    }
}

