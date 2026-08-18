package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.ordemservico.application.dtos.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.domain.events.OrdemServicoFinalizadaEvent;
import br.com.fiap.oficina.ordemservico.domain.entities.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.entities.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.entities.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrcamento;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

        var ordem = service.criar(new CriarOrdemServicoCommand(clienteId, veiculoId, List.of(), List.of(), "Revisao"));

        assertNotNull(ordem.id());
        assertEquals(new ClienteId(clienteId), ordem.clienteId());
        assertEquals(new VeiculoId(veiculoId), ordem.veiculoId());
        assertSame(ordem, ordemRepository.salvo);
        assertNotNull(ordem.orcamento());
        assertEquals(ordem.id(), ordem.orcamento().ordemServicoId());
        assertNotEquals(ordem.id().value(), ordem.orcamento().id().value());
    }

    @Test
    void criarComItensIniciaisMontaOrcamento() {
        var ordemRepository = new FakeOrdemServicoRepository(Optional.empty());
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.empty());
        var servicoGateway = new FakeServicoRepository(Optional.of(Servico.criar("SRV-001", "Troca de oleo", BigDecimal.TEN, 30)));
        var itemEstoque = ItemEstoque.criar("PEC-001", "Filtro", BigDecimal.TEN, BigDecimal.TEN);
        var service = new OrdemServicoApplicationService(
                ordemRepository,
                orcamentoGateway,
                servicoGateway,
                new FakeEstoqueRepository(Optional.of(itemEstoque)),
                new FakeVerificadorEstoque(true),
                evento -> {});
        var command = new CriarOrdemServicoCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new CriarOrdemServicoCommand.ItemServicoCommand("SRV-001", BigDecimal.ONE)),
                List.of(new CriarOrdemServicoCommand.ItemPecaCommand("PEC-001", BigDecimal.valueOf(2))),
                "Revisao");

        service.criar(command);

        assertEquals(1, orcamentoGateway.salvo.itensServico().size());
        assertEquals(1, orcamentoGateway.salvo.itensPeca().size());
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

        assertEquals(List.of(ordem), service.consultarPorCliente(ordem.clienteId().value()));
        assertEquals(List.of(ordem), service.consultarOrdens(StatusOrdemServico.RECEBIDA));
        assertEquals(List.of(ordem), service.consultarOrdens(null));
    }

    @Test
    void consultarTempoMedioExecucaoCalculaMediaEntreRecebimentoEFinalizacao() {
        var recebidaEm = OffsetDateTime.parse("2026-06-30T08:00:00Z");
        var ordemFinalizada = new OrdemServico(
                OrdemServicoId.novo(),
                null,
                new ClienteId(UUID.randomUUID()),
                new VeiculoId(UUID.randomUUID()),
                StatusOrdemServico.FINALIZADA,
                "Revisao",
                null,
                recebidaEm,
                null,
                recebidaEm.plusHours(2),
                null,
                false,
                null);
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordemFinalizada)), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertEquals("2:00", service.consultarTempoMedioExecucao());
    }

    @Test
    void consultarTempoMedioExecucaoRetornaZeroQuandoNaoHaFinalizadas() {
        var service = service(new FakeOrdemServicoRepository(Optional.of(novaOrdem())), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertEquals("0:00", service.consultarTempoMedioExecucao());
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
    void registrarDiagnosticoNovamenteMantemIdExistente() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        ordem.registrarDiagnostico(br.com.fiap.oficina.ordemservico.domain.entities.Diagnostico.registrar("Descricao inicial"));
        var diagnosticoId = ordem.diagnostico().id();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        var atualizada = service.registrarDiagnostico(new RegistrarDiagnosticoCommand(ordem.id().value(), "Descricao revisada"));

        assertEquals(diagnosticoId, atualizada.diagnostico().id());
        assertEquals("Descricao revisada", atualizada.diagnostico().descricao());
        assertSame(ordem, repository.salvo);
    }

    @Test
    void iniciarExecucaoExigeOrcamentoAprovado() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        orcamento.aprovar();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.of(orcamento)), new ArrayList<>());

        var atualizada = service.iniciarExecucao(ordem.id().value());

        assertEquals(StatusOrdemServico.EM_EXECUCAO, atualizada.status());
    }

    @Test
    void iniciarExecucaoRejeitaOrcamentoNaoAprovado() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
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
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(ordemRepository, orcamentoGateway, new ArrayList<>());

        var ajustada = service.pedirAjuste(ordem.id().value());

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ajustada.status());
        assertEquals(StatusOrcamento.ABERTO, orcamentoGateway.salvo.status());
    }

    @Test
    void pedirAjusteReabreOrcamentoDuranteExecucao() {
        var ordem = novaOrdemEmExecucao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        orcamento.aprovar();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var eventos = new ArrayList<>();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), orcamentoGateway, eventos);

        var ajustada = service.pedirAjuste(ordem.id().value());

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, ajustada.status());
        assertEquals(StatusOrcamento.ABERTO, orcamentoGateway.salvo.status());
        assertTrue(eventos.isEmpty());
    }

    @Test
    void atualizarStatusIniciaDiagnostico() {
        var ordem = novaOrdem();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        var atualizada = service.atualizarStatus(ordem.id().value(), StatusOrdemServico.EM_DIAGNOSTICO);

        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, atualizada.status());
        assertSame(ordem, repository.salvo);
    }

    @Test
    void atualizarStatusParaAguardandoAprovacaoFechaOrcamento() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        var orcamento = Orcamento.novo(ordem.id());
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(ordemRepository, orcamentoGateway, new ArrayList<>());

        var atualizada = service.atualizarStatus(ordem.id().value(), StatusOrdemServico.AGUARDANDO_APROVACAO);

        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, atualizada.status());
        assertEquals(StatusOrcamento.ENVIADO, orcamentoGateway.salvo.status());
    }

    @Test
    void atualizarStatusIgualNaoSalvaNovamente() {
        var ordem = novaOrdem();
        var repository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        var atualizada = service.atualizarStatus(ordem.id().value(), StatusOrdemServico.RECEBIDA);

        assertSame(ordem, atualizada);
        assertNull(repository.salvo);
    }

    @Test
    void atualizarStatusRejeitaStatusNulo() {
        var service = service(new FakeOrdemServicoRepository(Optional.of(novaOrdem())), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.atualizarStatus(UUID.randomUUID(), null));
    }

    @Test
    void atualizarStatusRejeitaRetornoParaRecebida() {
        var ordem = novaOrdem();
        ordem.iniciarDiagnostico();
        var service = service(new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeOrcamentoRepository(Optional.empty()), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.atualizarStatus(ordem.id().value(), StatusOrdemServico.RECEBIDA));
    }

    private static OrdemServicoApplicationService service(
            FakeOrdemServicoRepository ordemRepository,
            FakeOrcamentoRepository orcamentoGateway,
            List<Object> eventos) {
        return new OrdemServicoApplicationService(
                ordemRepository,
                orcamentoGateway,
                new FakeServicoRepository(Optional.empty()),
                new FakeEstoqueRepository(Optional.empty()),
                new FakeVerificadorEstoque(true),
                eventos::add);
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

    private static class FakeOrdemServicoRepository implements OrdemServicoGateway {
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

    private static class FakeOrcamentoRepository implements OrcamentoGateway {
        private final Optional<Orcamento> busca;
        private Orcamento salvo;

        FakeOrcamentoRepository(Optional<Orcamento> busca) {
            this.busca = busca;
        }

        @Override public Orcamento salvar(Orcamento orcamento) { this.salvo = orcamento; return orcamento; }
        @Override public Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId) { return busca.filter(orcamento -> orcamento.id().equals(orcamentoId)); }
        @Override public Optional<Orcamento> buscarPorOrdemServicoId(OrdemServicoId ordemServicoId) { return busca.filter(orcamento -> orcamento.ordemServicoId().equals(ordemServicoId)); }
    }

    private static class FakeServicoRepository implements ServicoGateway {
        private final Optional<Servico> busca;

        FakeServicoRepository(Optional<Servico> busca) {
            this.busca = busca;
        }

        @Override public boolean existePorCodigo(String codigo) { return busca.map(Servico::codigo).filter(codigo::equals).isPresent(); }
        @Override public Servico salvar(Servico servico) { return servico; }
        @Override public Optional<Servico> buscarPorId(ServicoId servicoId) { return busca.filter(servico -> servico.id().equals(servicoId)); }
        @Override public Optional<Servico> buscarPorCodigo(String codigo) { return busca.filter(servico -> servico.codigo().equals(codigo)); }
        @Override public List<Servico> buscarTodos() { return busca.stream().toList(); }
        @Override public void excluirPorId(ServicoId servicoId) {}
    }

    private static class FakeEstoqueRepository implements EstoqueGateway {
        private final Optional<ItemEstoque> busca;

        FakeEstoqueRepository(Optional<ItemEstoque> busca) {
            this.busca = busca;
        }

        @Override public boolean existePorCodigo(String codigo) { return busca.map(ItemEstoque::codigo).filter(codigo::equals).isPresent(); }
        @Override public ItemEstoque salvar(ItemEstoque itemEstoque) { return itemEstoque; }
        @Override public Optional<ItemEstoque> buscarPorId(ItemEstoqueId itemEstoqueId) { return busca.filter(item -> item.id().equals(itemEstoqueId)); }
        @Override public Optional<ItemEstoque> buscarPorCodigo(String codigo) { return busca.filter(item -> item.codigo().equals(codigo)); }
        @Override public List<ItemEstoque> buscarTodos() { return busca.stream().toList(); }
        @Override public List<ItemEstoque> buscarTodosAtivos() { return busca.filter(ItemEstoque::ativo).stream().toList(); }
    }

    private record FakeVerificadorEstoque(boolean disponivel) implements VerificadorEstoqueGateway {
        @Override public boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca) {
            return disponivel;
        }
    }
}

