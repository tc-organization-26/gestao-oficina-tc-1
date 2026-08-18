package br.com.fiap.oficina.ordemservico.application.usecases.interactors;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.NotificarAprovacaoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.domain.events.FaltaPecaEstoqueEvent;
import br.com.fiap.oficina.ordemservico.domain.events.OrcamentoFechadoEvent;
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
        var ordem = new OrdemServico(
                new OrdemServicoId(ordemId),
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
        var service = service(repository, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.of(servico)), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordemId, "SRV-001", java.math.BigDecimal.ONE));

        assertEquals(new OrdemServicoId(ordemId), repository.salvo.ordemServicoId());
        assertNotEquals(ordemId, repository.salvo.id().value());
        assertEquals(1, repository.salvo.itensServico().size());
        assertEquals(servico.id(), repository.salvo.itensServico().get(0).servicoId());
        assertEquals(StatusOrcamento.ABERTO, repository.salvo.status());
        assertSame(ordem, retornada);
    }

    @Test
    void adicionarItemServicoRejeitaOrcamentoInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.of(novoServico("SRV-001"))), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(UUID.randomUUID(), "SRV-001", java.math.BigDecimal.ONE)));
    }

    @Test
    void adicionarItemServicoRejeitaServicoInexistente() {
        var ordemId = UUID.randomUUID();
        var orcamento = Orcamento.novo(new OrdemServicoId(ordemId));
        var service = service(new FakeOrcamentoRepository(Optional.of(orcamento)), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordemId, "SRV-INEXISTENTE", java.math.BigDecimal.ONE)));
    }

    @Test
    void adicionarItemPecaBuscaPorCodigoESalvaAbertoQuandoEstoqueDisponivel() {
        var ordem = novaOrdemEmDiagnostico();
        var itemEstoque = novoItemEstoque("PEC-001");
        var eventos = new ArrayList<>();
        var repository = new FakeOrcamentoRepository(Optional.of(Orcamento.novo(ordem.id())));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(repository, ordemRepository, new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.of(itemEstoque)), new FakeVerificadorEstoque(true), eventos);

        var retornada = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordem.id().value(), "PEC-001", java.math.BigDecimal.valueOf(2)));

        assertSame(ordem, retornada);
        assertEquals(1, repository.salvo.itensPeca().size());
        assertEquals(itemEstoque.id(), repository.salvo.itensPeca().get(0).itemEstoqueId());
        assertEquals(StatusOrcamento.ABERTO, repository.salvo.status());
        assertNull(repository.salvo.dataFechamento());
        assertNull(ordemRepository.salvo);
        assertTrue(eventos.isEmpty());
    }

    @Test
    void adicionarItemPecaRejeitaItemEstoqueInexistente() {
        var ordemId = UUID.randomUUID();
        var orcamento = Orcamento.novo(new OrdemServicoId(ordemId));
        var service = service(new FakeOrcamentoRepository(Optional.of(orcamento)), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordemId, "PEC-INEXISTENTE", java.math.BigDecimal.valueOf(2))));
    }

    @Test
    void adicionarItemServicoNaoFechaOrcamentoMesmoQuandoPecasExistentesEstaoDisponiveis() {
        var ordem = novaOrdemEmDiagnostico();
        var servico = novoServico("SRV-002");
        var orcamentoExistente = Orcamento.novo(ordem.id());
        orcamentoExistente.adicionarItemPeca(new ItemPeca(new ItemEstoqueId(UUID.randomUUID()), java.math.BigDecimal.ONE));
        var eventos = new ArrayList<>();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamentoExistente));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(orcamentoGateway, ordemRepository, new FakeServicoRepository(Optional.of(servico)), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), eventos);

        var retornada = service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordem.id().value(), "SRV-002", java.math.BigDecimal.ONE));

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.ABERTO, orcamentoGateway.salvo.status());
        assertNull(ordemRepository.salvo);
        assertTrue(eventos.isEmpty());
    }

    @Test
    void adicionarItemPecaMarcaParaVerificacaoEPublicaEventoQuandoEstoqueIndisponivel() {
        var ordem = novaOrdemEmDiagnostico();
        var ordemId = ordem.id().value();
        var itemEstoque = novoItemEstoque("PEC-001");
        var eventos = new ArrayList<>();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(Orcamento.novo(new OrdemServicoId(ordemId))));
        var service = service(orcamentoGateway, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.of(itemEstoque)), new FakeVerificadorEstoque(false), eventos);

        var retornada = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(ordemId, "PEC-001", java.math.BigDecimal.valueOf(2)));

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.ABERTO, orcamentoGateway.salvo.status());
        assertEquals(1, eventos.size());
        assertInstanceOf(FaltaPecaEstoqueEvent.class, eventos.get(0));
    }

    @Test
    void fecharOrcamentoEnviaOrdemESalvaEvento() {
        var ordem = novaOrdemEmDiagnostico();
        var orcamento = Orcamento.novo(ordem.id());
        var eventos = new ArrayList<>();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(orcamentoGateway, ordemRepository, new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), eventos);

        var retornada = service.fechar(new FecharOrcamentoCommand(ordem.id().value()));

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.ENVIADO, orcamentoGateway.salvo.status());
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
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoGateway, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.aprovar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.APROVADO, orcamentoGateway.salvo.status());
    }

    @Test
    void recusarOrcamentoSalvaRecusaERetornaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoGateway, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.recusar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.RECUSADO, orcamentoGateway.salvo.status());
    }

    @Test
    void aprovarRejeitaOrcamentoInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.aprovar(UUID.randomUUID()));
    }

    @Test
    void notificarAprovacaoExternaAprovaOrcamento() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoGateway, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.notificarAprovacao(new NotificarAprovacaoOrcamentoCommand(
                ordem.id().value(),
                NotificarAprovacaoOrcamentoCommand.DecisaoOrcamento.APROVADO,
                "whatsapp",
                "protocolo-1"));

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.APROVADO, orcamentoGateway.salvo.status());
    }

    @Test
    void notificarAprovacaoExternaRecusaOrcamento() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = Orcamento.novo(ordem.id());
        orcamento.fechar();
        var orcamentoGateway = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoGateway, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.notificarAprovacao(new NotificarAprovacaoOrcamentoCommand(
                ordem.id().value(),
                NotificarAprovacaoOrcamentoCommand.DecisaoOrcamento.RECUSADO,
                "email",
                "protocolo-2"));

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.RECUSADO, orcamentoGateway.salvo.status());
    }

    @Test
    void notificarAprovacaoExternaRejeitaDecisaoNula() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeServicoRepository(Optional.empty()), new FakeEstoqueRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.notificarAprovacao(new NotificarAprovacaoOrcamentoCommand(
                UUID.randomUUID(),
                null,
                "whatsapp",
                "protocolo-3")));
    }

    private static OrcamentoApplicationService service(
            FakeOrcamentoRepository orcamentoGateway,
            FakeOrdemServicoRepository ordemRepository,
            FakeServicoRepository servicoGateway,
            FakeEstoqueRepository estoqueGateway,
            FakeVerificadorEstoque verificadorEstoque,
            List<Object> eventos) {
        return new OrcamentoApplicationService(orcamentoGateway, ordemRepository, servicoGateway, estoqueGateway, verificadorEstoque, eventos::add);
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