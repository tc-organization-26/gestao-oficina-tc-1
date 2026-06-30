package br.com.fiap.oficina.ordemservico.application.service;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.VerificadorEstoquePort;
import br.com.fiap.oficina.ordemservico.domain.event.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.event.OrcamentoFechadoEvent;
import br.com.fiap.oficina.ordemservico.domain.model.ItemPeca;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrcamento;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;
import br.com.fiap.oficina.servico.application.port.out.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoApplicationServiceTest {

    @Test
    void adicionarItemServicoUsaOrcamentoExistenteESalvaAberto() {
        var ordemId = UUID.randomUUID();
        var orcamentoExistente = Orcamento.novo(new OrdemServicoId(ordemId));
        var repository = new FakeOrcamentoRepository(Optional.of(orcamentoExistente));
        var servico = novoServico("SRV-001");
        var service = service(repository, new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.of(servico)), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var orcamento = service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordemId, "SRV-001", 1.0));

        assertEquals(new OrdemServicoId(ordemId), orcamento.ordemServicoId());
        assertNotEquals(ordemId, orcamento.id().value());
        assertEquals(1, orcamento.itensServico().size());
        assertEquals(servico.id(), orcamento.itensServico().get(0).servicoId());
        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertSame(orcamento, repository.salvo);
    }

    @Test
    void adicionarItemServicoRejeitaOrcamentoInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.of(novoServico("SRV-001"))), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(UUID.randomUUID(), "SRV-001", 1.0)));
    }

    @Test
    void adicionarItemServicoRejeitaServicoInexistente() {
        var ordemId = UUID.randomUUID();
        var orcamento = Orcamento.novo(new OrdemServicoId(ordemId));
        var service = service(new FakeOrcamentoRepository(Optional.of(orcamento)), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordemId, "SRV-INEXISTENTE", 1.0)));
    }

    @Test
    void adicionarItemPecaBuscaPorCodigoESalvaAbertoQuandoEstoqueDisponivel() {
        var ordem = novaOrdemEmDiagnostico();
        var itemEstoque = novoItemEstoque("PEC-001");
        var eventos = new ArrayList<>();
        var repository = new FakeOrcamentoRepository(Optional.of(Orcamento.novo(ordem.id())));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, ordemRepository, new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.of(itemEstoque)), new FakeVerificadorEstoque(true), eventos);

        var orcamento = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordem.id().value(), "PEC-001", 2.0));

        assertEquals(1, orcamento.itensPeca().size());
        assertEquals(itemEstoque.id(), orcamento.itensPeca().get(0).itemEstoqueId());
        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertNull(orcamento.dataFechamento());
        assertNull(ordemRepository.salvo);
        assertTrue(eventos.isEmpty());
    }

    @Test
    void adicionarItemPecaRejeitaItemEstoqueInexistente() {
        var ordemId = UUID.randomUUID();
        var orcamento = Orcamento.novo(new OrdemServicoId(ordemId));
        var service = service(new FakeOrcamentoRepository(Optional.of(orcamento)), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordemId, "PEC-INEXISTENTE", 2.0)));
    }

    @Test
    void adicionarItemServicoNaoFechaOrcamentoMesmoQuandoPecasExistentesEstaoDisponiveis() {
        var ordem = novaOrdemEmDiagnostico();
        var servico = novoServico("SRV-002");
        var orcamentoExistente = Orcamento.novo(ordem.id());
        orcamentoExistente.adicionarItemPeca(new ItemPeca(new ItemEstoqueId(UUID.randomUUID()), 1.0));
        var eventos = new ArrayList<>();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamentoExistente));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(orcamentoRepository, ordemRepository, new FakeServicoRepository(Optional.of(servico)), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), eventos);

        var orcamento = service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordem.id().value(), "SRV-002", 1.0));

        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertNull(ordemRepository.salvo);
        assertTrue(eventos.isEmpty());
    }

    @Test
    void adicionarItemPecaMarcaParaVerificacaoEPublicaEventoQuandoEstoqueIndisponivel() {
        var ordemId = UUID.randomUUID();
        var itemEstoque = novoItemEstoque("PEC-001");
        var eventos = new ArrayList<>();
        var service = service(new FakeOrcamentoRepository(Optional.of(Orcamento.novo(new OrdemServicoId(ordemId)))), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.of(itemEstoque)), new FakeVerificadorEstoque(false), eventos);

        var orcamento = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordemId, "PEC-001", 2.0));

        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertEquals(1, eventos.size());
        assertInstanceOf(FaltaPecaEstoqueEvent.class, eventos.get(0));
    }

    @Test
    void fecharOrcamentoEnviaOrdemESalvaEvento() {
        var ordem = novaOrdemEmDiagnostico();
        var orcamento = Orcamento.novo(ordem.id());
        var eventos = new ArrayList<>();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(orcamentoRepository, ordemRepository, new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), eventos);

        service.fechar(new FecharOrcamentoCommand(ordem.id().value()));

        assertEquals(StatusOrcamento.ENVIADO, orcamentoRepository.salvo.status());
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordemRepository.salvo.status());
        assertEquals(1, eventos.size());
        assertInstanceOf(OrcamentoFechadoEvent.class, eventos.get(0));
    }

    @Test
    void fecharOrcamentoRejeitaOrdemInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.fechar(new FecharOrcamentoCommand(UUID.randomUUID())));
    }

    @Test
    void aprovarOrcamentoSalvaAprovacaoERetornaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoRepository, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.aprovar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.APROVADO, orcamentoRepository.salvo.status());
    }

    @Test
    void recusarOrcamentoSalvaRecusaERetornaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoRepository, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.recusar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.RECUSADO, orcamentoRepository.salvo.status());
    }

    @Test
    void aprovarRejeitaOrcamentoInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.aprovar(UUID.randomUUID()));
    }

    private static OrcamentoApplicationService service(
            FakeOrcamentoRepository orcamentoRepository,
            FakeOrdemServicoRepository ordemRepository,
            FakeServicoRepository servicoRepository,
            FakeEstoqueRepository estoqueRepository,
            FakeVerificadorEstoque verificadorEstoque,
            List<Object> eventos) {
        return new OrcamentoApplicationService(orcamentoRepository, ordemRepository, servicoRepository, estoqueRepository, verificadorEstoque, eventos::add);
    }

    private static OrdemServico novaOrdemEmDiagnostico() {
        var ordem = OrdemServico.criar(new ClienteId(UUID.randomUUID()), new VeiculoId(UUID.randomUUID()), "Revisao");
        ordem.iniciarDiagnostico();
        return ordem;
    }

    private static OrdemServico novaOrdemAguardandoAprovacao() {
        var ordem = novaOrdemEmDiagnostico();
        ordem.finalizarOrcamento();
        return ordem;
    }

    private static Servico novoServico(String codigo) {
        return Servico.criar(codigo, "Troca de oleo", BigDecimal.TEN, 30);
    }

    private static ItemEstoque novoItemEstoque(String codigo) {
        return ItemEstoque.criar(codigo, "Pastilha de freio", BigDecimal.TEN, BigDecimal.TEN);
    }

    private static class FakeOrcamentoRepository implements OrcamentoRepositoryPort {
        private final Optional<Orcamento> busca;
        private Orcamento salvo;

        FakeOrcamentoRepository(Optional<Orcamento> busca) {
            this.busca = busca;
        }

        @Override public Orcamento salvar(Orcamento orcamento) { this.salvo = orcamento; return orcamento; }
        @Override public Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId) { return busca.filter(orcamento -> orcamento.id().equals(orcamentoId)); }
        @Override public Optional<Orcamento> buscarPorOrdemServicoId(OrdemServicoId ordemServicoId) { return busca.filter(orcamento -> orcamento.ordemServicoId().equals(ordemServicoId)); }
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

    private static class FakeServicoRepository implements ServicoRepositoryPort {
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

    private static class FakeEstoqueRepository implements EstoqueRepositoryPort {
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

    private record FakeVerificadorEstoque(boolean disponivel) implements VerificadorEstoquePort {
        @Override public boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca) {
            return disponivel;
        }
    }
}