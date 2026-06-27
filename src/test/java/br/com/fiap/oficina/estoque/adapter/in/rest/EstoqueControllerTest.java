package br.com.fiap.oficina.estoque.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.estoque.adapter.in.rest.request.AtualizarItemEstoqueRequest;
import br.com.fiap.oficina.estoque.adapter.in.rest.request.CadastrarItemEstoqueRequest;
import br.com.fiap.oficina.estoque.adapter.in.rest.request.MovimentarEstoqueRequest;
import br.com.fiap.oficina.estoque.application.command.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.CadastrarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.port.in.AtualizarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.CadastrarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarTodosItensEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ExcluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.IncluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EstoqueControllerTest {
    @Test
    void cadastrarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.cadastrar(new CadastrarItemEstoqueRequest("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE));

        assertEquals("OLEO", response.codigo());
    }

    @Test
    void consultarTodosRetornaResponses() {
        var controller = controller(new ExclusaoFake());

        var responses = controller.consultarTodos();

        assertEquals(1, responses.size());
        assertEquals("OLEO", responses.get(0).codigo());
    }

    @Test
    void atualizarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.atualizar(UUID.randomUUID(), new AtualizarItemEstoqueRequest("Filtro", BigDecimal.valueOf(20)));

        assertEquals("Filtro", response.descricao());
    }

    @Test
    void incluirRetornaQuantidadeAtualizada() {
        var controller = controller(new ExclusaoFake());

        var response = controller.incluir(UUID.randomUUID(), new MovimentarEstoqueRequest(BigDecimal.valueOf(2)));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(response.quantidadeDisponivel()));
    }

    @Test
    void baixarRetornaQuantidadeAtualizada() {
        var controller = controller(new ExclusaoFake());

        var response = controller.baixar(UUID.randomUUID(), new MovimentarEstoqueRequest(BigDecimal.ONE));

        assertEquals(0, BigDecimal.ZERO.compareTo(response.quantidadeDisponivel()));
    }

    @Test
    void excluirEncaminhaIdParaUseCase() {
        var exclusao = new ExclusaoFake();
        var controller = controller(exclusao);
        var id = UUID.randomUUID();

        controller.excluir(id);

        assertEquals(id, exclusao.excluido.value());
    }

    private static EstoqueController controller(ExclusaoFake exclusao) {
        return new EstoqueController(new CadastroFake(), new ConsultaFake(), new ConsultaTodosFake(), new AtualizacaoFake(), new InclusaoFake(), new BaixaFake(), exclusao);
    }

    private static class CadastroFake implements CadastrarItemEstoqueUseCase {
        @Override public ItemEstoque cadastrar(CadastrarItemEstoqueCommand command) { return ItemEstoque.criar(command.codigo(), command.descricao(), command.valorUnitario(), command.quantidadeInicial()); }
    }
    private static class ConsultaFake implements ConsultarItemEstoqueUseCase {
        @Override public ItemEstoque consultarPorId(ItemEstoqueId itemEstoqueId) { return ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE); }
    }
    private static class ConsultaTodosFake implements ConsultarTodosItensEstoqueUseCase {
        @Override public List<ItemEstoque> consultarTodos() { return List.of(ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE)); }
    }
    private static class AtualizacaoFake implements AtualizarItemEstoqueUseCase {
        @Override public ItemEstoque atualizar(AtualizarItemEstoqueCommand command) { return ItemEstoque.criar("OLEO", command.descricao(), command.valorUnitario(), BigDecimal.ONE); }
    }
    private static class InclusaoFake implements IncluirItemEstoqueUseCase {
        @Override public ItemEstoque incluir(IncluirItemEstoqueCommand command) { return ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE.add(command.quantidade())); }
    }
    private static class BaixaFake implements BaixarItemEstoqueUseCase {
        @Override public ItemEstoque baixar(BaixarItemEstoqueCommand command) { return ItemEstoque.criar("OLEO", "Oleo", BigDecimal.TEN, BigDecimal.ONE.subtract(command.quantidade())); }
    }
    private static class ExclusaoFake implements ExcluirItemEstoqueUseCase {
        private ItemEstoqueId excluido;
        @Override public void excluir(ItemEstoqueId itemEstoqueId) { this.excluido = itemEstoqueId; }
    }
}