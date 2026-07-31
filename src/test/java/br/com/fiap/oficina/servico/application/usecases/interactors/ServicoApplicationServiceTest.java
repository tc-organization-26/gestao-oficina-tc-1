package br.com.fiap.oficina.servico.application.usecases.interactors;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.servico.application.dtos.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.application.dtos.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.application.gateways.ServicoRepositoryPort;
import br.com.fiap.oficina.servico.domain.entities.Servico;
import br.com.fiap.oficina.servico.domain.valueobjects.ServicoId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServicoApplicationServiceTest {
    @Test
    void cadastrarSalvaServicoNovo() {
        var repository = new FakeRepository(false, Optional.empty());
        var service = new ServicoApplicationService(repository);

        var servico = service.cadastrar(new CadastrarServicoCommand("TROCA", "Troca", BigDecimal.TEN, 60));

        assertNotNull(servico.id());
        assertSame(servico, repository.salvo);
    }

    @Test
    void cadastrarRejeitaCodigoDuplicado() {
        var service = new ServicoApplicationService(new FakeRepository(true, Optional.empty()));

        assertThrows(DomainException.class,
                () -> service.cadastrar(new CadastrarServicoCommand("TROCA", "Troca", BigDecimal.TEN, 60)));
    }

    @Test
    void atualizarBuscaAlteraESalva() {
        var existente = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);
        var service = new ServicoApplicationService(new FakeRepository(false, Optional.of(existente)));

        var atualizado = service.atualizar(new AtualizarServicoCommand(existente.id().value(), "Alinhamento", BigDecimal.valueOf(120), 90));

        assertEquals("Alinhamento", atualizado.descricao());
    }

    @Test
    void consultarRejeitaServicoInexistente() {
        var service = new ServicoApplicationService(new FakeRepository(false, Optional.empty()));

        assertThrows(DomainException.class, () -> service.consultarPorId(new ServicoId(UUID.randomUUID())));
    }

    @Test
    void consultarTodosRetornaServicosDoRepositorio() {
        var existente = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);
        var service = new ServicoApplicationService(new FakeRepository(false, Optional.of(existente), List.of(existente)));

        var servicos = service.consultarTodos();

        assertEquals(List.of(existente), servicos);
    }

    @Test
    void excluirValidaExistenciaEExcluiNoRepositorio() {
        var existente = Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60);
        var repository = new FakeRepository(false, Optional.of(existente));
        var service = new ServicoApplicationService(repository);

        service.excluir(existente.id());

        assertEquals(existente.id(), repository.excluido);
    }

    private static class FakeRepository implements ServicoRepositoryPort {
        private final boolean existe;
        private final Optional<Servico> busca;
        private final List<Servico> todos;
        private Servico salvo;
        private ServicoId excluido;

        FakeRepository(boolean existe, Optional<Servico> busca) {
            this(existe, busca, List.of());
        }

        FakeRepository(boolean existe, Optional<Servico> busca, List<Servico> todos) {
            this.existe = existe;
            this.busca = busca;
            this.todos = todos;
        }

        @Override public boolean existePorCodigo(String codigo) { return existe; }
        @Override public Servico salvar(Servico servico) { this.salvo = servico; return servico; }
        @Override public Optional<Servico> buscarPorId(ServicoId servicoId) { return busca; }
        @Override public Optional<Servico> buscarPorCodigo(String codigo) { return busca; }
        @Override public List<Servico> buscarTodos() { return todos; }
        @Override public void excluirPorId(ServicoId servicoId) { this.excluido = servicoId; }
    }
}