package br.com.fiap.oficina.ordemservico.application.service;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.event.OrdemServicoFinalizadaEvent;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrcamento;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrdemServicoApplicationServiceTest {

    @Test
    void criarSalvaOrdemNova() {
        var ordemRepository = new FakeOrdemServicoRepository(Optional.empty());
        var service = service(ordemRepository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());
        var clienteId = UUID.randomUUID();
        var veiculoId = UUID.randomUUID();

        var ordem = service.criar(new CriarOrdemServicoCommand(clienteId, veiculoId, "Revisao"));

        assertNotNull(ordem.id());
        assertEquals(new ClienteId(clienteId), ordem.clienteId());
        assertEquals(new VeiculoId(veiculoId), ordem.veiculoId());
        assertSame(ordem, ordemRepository.salvo);
    }

    @Test
    void consultarRetornaOrdemDoRepositorio() {
        var ordem = novaOrdem();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertSame(ordem, service.consultarPorId(ordem.id()));
    }

    @Test
    void consultarRejeitaOrdemInexistente() {
        var service = service(new FakeOrdemServicoRepository(Optional.empty()), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.consultarPorId(OrdemServicoId.novo()));
    }

    @Test
    void consultarListasDelegamAoRepositorio() {
        var ordem = novaOrdem();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertEquals(List.of(ordem), service.consultarHistoricoPorCliente(ordem.clienteId().value()));
        assertEquals(List.of(ordem), service.consultarOrdens(StatusOrdemServico.RECEBIDA));
        assertEquals(List.of(ordem), service.consultarOrdens(null));
    }

    @Test
    void iniciarDiagnosticoAlteraStatusESalva() {
        var ordem = novaOrdem();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        var atualizada = service.iniciarDiagnostico(ordem.id());

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, atualizada.status());
        assertSame(ordem, repository.salvo);
    }

    @Test
    void registrarDiagnosticoPreencheDiagnosticoESalva() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        var atualizada = service.registrarDiagnostico(new RegistrarDiagnosticoCommand(ordem.id().value(), "Trocar freios"));

        assertEquals("Trocar freios", atualizada.diagnostico().descricao());
        assertSame(ordem, repository.salvo);
    }

    @Test
    void iniciarExecucaoExigeOrcamentoAprovado() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        orcamento.fechar();
        orcamento.aprovar();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.of(orcamento)), new ArrayList<>());

        var atualizada = service.iniciarExecucao(ordem.id().value());

        assertEquals(StatusOrdemServico.EM_EXECUCAO, atualizada.status());
    }

    @Test
    void iniciarExecucaoRejeitaOrcamentoNaoAprovado() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.of(orcamento)), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.iniciarExecucao(ordem.id().value()));
    }

    @Test
    void finalizarSalvaEPublicaEvento() {
        var ordem = novaOrdemEmExecucao();
        var eventos = new ArrayList<>();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), eventos);

        var finalizada = service.finalizar(ordem.id().value());

        assertEquals(StatusOrdemServico.FINALIZADA, finalizada.status());
        assertSame(ordem, repository.salvo);
        assertEquals(1, eventos.size());
        assertInstanceOf(OrdemServicoFinalizadaEvent.class, eventos.get(0));
    }

    @Test
    void registrarPagamentoEEntregarSalvamTransicoes() {
        var ordem = novaOrdemFinalizada();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        service.registrarPagamento(ordem.id().value());
        var entregue = service.entregar(ordem.id().value());

        assertTrue(entregue.pago());
        assertEquals(StatusOrdemServico.ENTREGUE, entregue.status());
        assertSame(ordem, repository.salvo);
    }

    @Test
    void pedirAjusteReabreOrcamentoESalvaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        orcamento.fechar();
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(ordemRepository, orcamentoRepository, new ArrayList<>());

        var ajustada = service.pedirAjuste(ordem.id().value());

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ajustada.status());
        assertEquals(StatusOrcamento.ABERTO, orcamentoRepository.salvo.status());
    }

    @Test
    void alterarOrcamentoReabreOrcamentoDuranteExecucao() {
        var ordem = novaOrdemEmExecucao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        orcamento.fechar();
        orcamento.aprovar();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), orcamentoRepository, new ArrayList<>());

        var alterada = service.alterarOrcamento(ordem.id().value());

        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, alterada.status());
        assertEquals(StatusOrcamento.ABERTO, orcamentoRepository.salvo.status());
    }

    private static OrdemServicoApplicationService service(
            FakeOrdemServicoRepository ordemRepository,
            FakeOrcamentoRepository orcamentoRepository,
            List<Object> eventos) {
        return new OrdemServicoApplicationService(ordemRepository, orcamentoRepository, eventos::add);
    }

    private static OrdemServico novaOrdem() {
        return OrdemServico.criar(new ClienteId(UUID.randomUUID()), new VeiculoId(UUID.randomUUID()), "Revisao");
    }

    private static OrdemServico novaOrdemAguardandoAprovacao() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        ordem.finalizarOrcamento();
        return ordem;
    }

    private static OrdemServico novaOrdemEmExecucao() {
        var ordem = novaOrdemAguardandoAprovacao();
        ordem.iniciarExecucao();
        return ordem;
    }

    private static OrdemServico novaOrdemFinalizada() {
        var ordem = novaOrdemEmExecucao();
        ordem.finalizar();
        return ordem;
    }

    private static class FakeOrdemServicoRepository implements OrdemServicoRepositoryPort {
        private final Optional<OrdemServico> busca;
        private OrdemServico salvo;

        FakeOrdemServicoRepository(Optional<OrdemServico> busca) {
            this.busca = busca;
        }

        @Override public OrdemServico salvar(OrdemServico ordemServico) { this.salvo = ordemServico; return ordemServico; }
        @Override public Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId) { return busca; }
        @Override public List<OrdemServico> buscarPorClienteOrdenado(UUID clienteId) { return busca.stream().toList(); }
        @Override public List<OrdemServico> buscarPorStatusOrdenado(Integer statusOrdemServico) { return busca.stream().toList(); }
        @Override public List<OrdemServico> buscarTodosOrdenado() { return busca.stream().toList(); }
    }

    private static class FakeOrcamentoRepository implements OrcamentoRepositoryPort {
        private final Optional<Orcamento> busca;
        private Orcamento salvo;

        FakeOrcamentoRepository(Optional<Orcamento> busca) {
            this.busca = busca;
        }

        @Override public Orcamento salvar(Orcamento orcamento) { this.salvo = orcamento; return orcamento; }
        @Override public Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId) { return busca; }
    }
}
