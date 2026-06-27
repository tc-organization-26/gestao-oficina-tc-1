package br.com.fiap.oficina.estoque.application.service;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.application.command.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.CadastrarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.DomainException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EstoqueApplicationServiceTest {
    @Test
    void cadastrarSalvaItemNovo() {
        var repository = new FakeRepository(false, Optional.empty());
        var service = new EstoqueApplicationService(repository);

        var item = service.cadastrar(new CadastrarItemEstoqueCommand("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE));

        assertNotNull(item.id());
        assertSame(item, repository.salvo);
    }

    @Test
    void cadastrarRejeitaCodigoDuplicado() {
        var service = new EstoqueApplicationService(new FakeRepository(true, Optional.empty()));

        assertThrows(DomainException.class,
                () -> service.cadastrar(new CadastrarItemEstoqueCommand("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE)));
    }

    @Test
    void atualizarBuscaAlteraESalva() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);
        var repository = new FakeRepository(false, Optional.of(item));
        var service = new EstoqueApplicationService(repository);

        var atualizado = service.atualizar(new AtualizarItemEstoqueCommand(item.id().value(), "Filtro", BigDecimal.valueOf(20)));

        assertEquals("Filtro", atualizado.descricao());
        assertSame(item, repository.salvo);
    }

    @Test
    void incluirSomaQuantidadeESalva() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);
        var service = new EstoqueApplicationService(new FakeRepository(false, Optional.of(item)));

        var atualizado = service.incluir(new IncluirItemEstoqueCommand(item.id().value(), BigDecimal.valueOf(2)));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(atualizado.quantidadeDisponivel()));
    }

    @Test
    void baixarSubtraiQuantidadeESalva() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.valueOf(5));
        var service = new EstoqueApplicationService(new FakeRepository(false, Optional.of(item)));

        var atualizado = service.baixar(new BaixarItemEstoqueCommand(item.id().value(), BigDecimal.valueOf(2)));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(atualizado.quantidadeDisponivel()));
    }

    @Test
    void consultarTodosRetornaItensDoRepositorio() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);
        var service = new EstoqueApplicationService(new FakeRepository(false, Optional.of(item), List.of(item)));

        assertEquals(List.of(item), service.consultarTodos());
    }

    @Test
    void excluirDesativaESalvaItem() {
        var item = ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE);
        var repository = new FakeRepository(false, Optional.of(item));
        var service = new EstoqueApplicationService(repository);

        service.excluir(item.id());

        assertFalse(repository.salvo.ativo());
    }

    @Test
    void consultarPorIdRejeitaItemInexistente() {
        var service = new EstoqueApplicationService(new FakeRepository(false, Optional.empty()));

        assertThrows(DomainException.class, () -> service.consultarPorId(new ItemEstoqueId(UUID.randomUUID())));
    }

    private static class FakeRepository implements EstoqueRepositoryPort {
        private final boolean existe;
        private final Optional<ItemEstoque> busca;
        private final List<ItemEstoque> todos;
        private ItemEstoque salvo;

        FakeRepository(boolean existe, Optional<ItemEstoque> busca) {
            this(existe, busca, List.of());
        }

        FakeRepository(boolean existe, Optional<ItemEstoque> busca, List<ItemEstoque> todos) {
            this.existe = existe;
            this.busca = busca;
            this.todos = todos;
        }

        @Override public boolean existePorCodigo(String codigo) { return existe; }
        @Override public ItemEstoque salvar(ItemEstoque itemEstoque) { this.salvo = itemEstoque; return itemEstoque; }
        @Override public Optional<ItemEstoque> buscarPorId(ItemEstoqueId itemEstoqueId) { return busca; }
        @Override public List<ItemEstoque> buscarTodos() { return todos; }
    }
}