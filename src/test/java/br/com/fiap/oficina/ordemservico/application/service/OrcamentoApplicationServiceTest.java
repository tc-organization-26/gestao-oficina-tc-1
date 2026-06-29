package br.com.fiap.oficina.ordemservico.application.service;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
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
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrcamentoApplicationServiceTest {

    @Test
    void adicionarItemServicoCriaOrcamentoQuandoNaoExisteESalvaAberto() {
        var repository = new FakeOrcamentoRepository(Optional.empty());
        var service = service(repository, new FakeOrdemServicoRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());
        var ordemId = UUID.randomUUID();

        var orcamento = service.adicionarItemServico(
                new AdicionarItemServicoOrcamentoCommand(ordemId, UUID.randomUUID(), 1.0));

        assertEquals(OrcamentoId.from(ordemId), orcamento.id());
        assertEquals(1, orcamento.itensServico().size());
        assertEquals(StatusOrcamento.ABERTO, orcamento.status());
        assertSame(orcamento, repository.salvo);
    }

    @Test
    void adicionarItemPecaFechaOrcamentoQuandoEstoqueDisponivel() {
        var repository = new FakeOrcamentoRepository(Optional.empty());
        var service = service(repository, new FakeOrdemServicoRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        var orcamento = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(UUID.randomUUID(), UUID.randomUUID(), 2.0));

        assertEquals(1, orcamento.itensPeca().size());
        assertEquals(StatusOrcamento.FINALIZADO, orcamento.status());
        assertNotNull(orcamento.dataFechamento());
    }

    @Test
    void adicionarItemPecaMarcaParaVerificacaoEPublicaEventoQuandoEstoqueIndisponivel() {
        var eventos = new ArrayList<>();
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeVerificadorEstoque(false), eventos);

        var orcamento = service.adicionarItemPeca(
                new AdicionarItemPecaOrcamentoCommand(UUID.randomUUID(), UUID.randomUUID(), 2.0));

        assertEquals(StatusOrcamento.AGUARDANDO_VERIFICACAO_ESTOQUE, orcamento.status());
        assertEquals(1, eventos.size());
        assertInstanceOf(FaltaPecaEstoqueEvent.class, eventos.get(0));
    }

    @Test
    void fecharOrcamentoFinalizaOrdemESalvaEvento() {
        var ordem = novaOrdemEmDiagnostico();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        var eventos = new ArrayList<>();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var ordemRepository = new FakeOrdemServicoRepository(Optional.of(ordem));
        var service = service(orcamentoRepository, ordemRepository, new FakeVerificadorEstoque(true), eventos);

        service.fechar(new FecharOrcamentoCommand(ordem.id().value()));

        assertEquals(StatusOrcamento.FINALIZADO, orcamentoRepository.salvo.status());
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordemRepository.salvo.status());
        assertEquals(1, eventos.size());
        assertInstanceOf(OrcamentoFechadoEvent.class, eventos.get(0));
    }

    @Test
    void fecharOrcamentoRejeitaOrdemInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.fechar(new FecharOrcamentoCommand(UUID.randomUUID())));
    }

    @Test
    void aprovarOrcamentoSalvaAprovacaoERetornaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        orcamento.fechar();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoRepository, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.aprovar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.APROVADO, orcamentoRepository.salvo.status());
    }

    @Test
    void recusarOrcamentoSalvaRecusaERetornaOrdem() {
        var ordem = novaOrdemAguardandoAprovacao();
        var orcamento = new Orcamento(ordem.id().value(), ordem.id());
        orcamento.fechar();
        var orcamentoRepository = new FakeOrcamentoRepository(Optional.of(orcamento));
        var service = service(orcamentoRepository, new FakeOrdemServicoRepository(Optional.of(ordem)), new FakeVerificadorEstoque(true), new ArrayList<>());

        var retornada = service.recusar(ordem.id().value());

        assertSame(ordem, retornada);
        assertEquals(StatusOrcamento.RECUSADO, orcamentoRepository.salvo.status());
    }

    @Test
    void aprovarRejeitaOrcamentoInexistente() {
        var service = service(new FakeOrcamentoRepository(Optional.empty()), new FakeOrdemServicoRepository(Optional.empty()), new FakeVerificadorEstoque(true), new ArrayList<>());

        assertThrows(DomainException.class, () -> service.aprovar(UUID.randomUUID()));
    }

    private static OrcamentoApplicationService service(
            FakeOrcamentoRepository orcamentoRepository,
            FakeOrdemServicoRepository ordemRepository,
            FakeVerificadorEstoque verificadorEstoque,
            List<Object> eventos) {
        return new OrcamentoApplicationService(orcamentoRepository, ordemRepository, verificadorEstoque, eventos::add);
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

    private static class FakeOrcamentoRepository implements OrcamentoRepositoryPort {
        private final Optional<Orcamento> busca;
        private Orcamento salvo;

        FakeOrcamentoRepository(Optional<Orcamento> busca) {
            this.busca = busca;
        }

        @Override public Orcamento salvar(Orcamento orcamento) { this.salvo = orcamento; return orcamento; }
        @Override public Optional<Orcamento> buscarPorId(OrcamentoId orcamentoId) { return busca; }
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

    private record FakeVerificadorEstoque(boolean disponivel) implements VerificadorEstoquePort {
        @Override public boolean temTodosOsItensDisponiveis(List<ItemPeca> itensPeca) {
            return disponivel;
        }
    }
}
