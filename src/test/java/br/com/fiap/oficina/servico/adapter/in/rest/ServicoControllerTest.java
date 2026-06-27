package br.com.fiap.oficina.servico.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.servico.adapter.in.rest.request.AtualizarServicoRequest;
import br.com.fiap.oficina.servico.adapter.in.rest.request.CadastrarServicoRequest;
import br.com.fiap.oficina.servico.application.command.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.application.command.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.application.port.in.AtualizarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.CadastrarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarTodosServicosUseCase;
import br.com.fiap.oficina.servico.application.port.in.ExcluirServicoUseCase;
import br.com.fiap.oficina.servico.domain.model.Servico;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ServicoControllerTest {
    @Test
    void cadastrarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.cadastrar(new CadastrarServicoRequest("TROCA", "Troca", BigDecimal.TEN, 60));

        assertEquals("TROCA", response.codigo());
    }

    @Test
    void atualizarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.atualizar(UUID.randomUUID(), new AtualizarServicoRequest("Alinhamento", BigDecimal.valueOf(120), 90));

        assertEquals("Alinhamento", response.descricao());
    }

    @Test
    void consultarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.consultarPorId(UUID.randomUUID());

        assertEquals("TROCA", response.codigo());
    }

    @Test
    void consultarTodosRetornaResponses() {
        var controller = controller(new ExclusaoFake());

        var responses = controller.consultarTodos();

        assertEquals(1, responses.size());
        assertEquals("TROCA", responses.get(0).codigo());
    }

    @Test
    void excluirEncaminhaIdParaUseCase() {
        var exclusao = new ExclusaoFake();
        var controller = controller(exclusao);
        var id = UUID.randomUUID();

        controller.excluir(id);

        assertEquals(id, exclusao.excluido.value());
    }

    private static ServicoController controller(ExclusaoFake exclusao) {
        return new ServicoController(new CadastroFake(), new AtualizacaoFake(), new ConsultaFake(), new ConsultaTodosFake(), exclusao);
    }

    private static class CadastroFake implements CadastrarServicoUseCase {
        @Override public Servico cadastrar(CadastrarServicoCommand command) { return Servico.criar(command.codigo(), command.descricao(), command.valorUnitario(), command.tempoEstimadoMinutos()); }
    }
    private static class AtualizacaoFake implements AtualizarServicoUseCase {
        @Override public Servico atualizar(AtualizarServicoCommand command) { return Servico.criar("TROCA", command.descricao(), command.valorUnitario(), command.tempoEstimadoMinutos()); }
    }
    private static class ConsultaFake implements ConsultarServicoUseCase {
        @Override public Servico consultarPorId(ServicoId servicoId) { return Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60); }
    }
    private static class ConsultaTodosFake implements ConsultarTodosServicosUseCase {
        @Override public List<Servico> consultarTodos() { return List.of(Servico.criar("TROCA", "Troca", BigDecimal.TEN, 60)); }
    }
    private static class ExclusaoFake implements ExcluirServicoUseCase {
        private ServicoId excluido;
        @Override public void excluir(ServicoId servicoId) { this.excluido = servicoId; }
    }
}