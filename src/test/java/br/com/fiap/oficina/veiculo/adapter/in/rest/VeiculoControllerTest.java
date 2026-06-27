package br.com.fiap.oficina.veiculo.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.veiculo.adapter.in.rest.request.AtualizarVeiculoRequest;
import br.com.fiap.oficina.veiculo.adapter.in.rest.request.CadastrarVeiculoRequest;
import br.com.fiap.oficina.veiculo.application.command.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.command.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.port.in.AtualizarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.CadastrarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarTodosVeiculosUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ExcluirVeiculoUseCase;
import br.com.fiap.oficina.veiculo.domain.model.Veiculo;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoPlaca;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VeiculoControllerTest {
    @Test
    void cadastrarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.cadastrar(new CadastrarVeiculoRequest(UUID.randomUUID().toString(), "ABC1D23", "Toyota", "Corolla", 2020));

        assertEquals("ABC1D23", response.placa());
    }

    @Test
    void atualizarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.atualizar(UUID.randomUUID(), new AtualizarVeiculoRequest("Civic", "Honda", 2021));

        assertEquals("Honda", response.marca());
    }

    @Test
    void consultarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.consultarPorId(UUID.randomUUID());

        assertEquals("Toyota", response.marca());
    }

    @Test
    void consultarTodosRetornaResponses() {
        var controller = controller(new ExclusaoFake());

        var responses = controller.consultarTodos();

        assertEquals(1, responses.size());
        assertEquals("Toyota", responses.get(0).marca());
    }

    @Test
    void excluirEncaminhaIdParaUseCase() {
        var exclusao = new ExclusaoFake();
        var controller = controller(exclusao);
        var id = UUID.randomUUID();

        controller.excluir(id);

        assertEquals(id, exclusao.excluido.value());
    }

    private static VeiculoController controller(ExclusaoFake exclusao) {
        return new VeiculoController(new CadastroFake(), new AtualizacaoFake(), new ConsultaFake(), new ConsultaTodosFake(), exclusao);
    }

    private static class CadastroFake implements CadastrarVeiculoUseCase {
        @Override public Veiculo cadastrar(CadastrarVeiculoCommand command) { return Veiculo.criar(new ClienteId(command.clienteId()), VeiculoPlaca.novo(command.placa()), command.marca(), command.modelo(), command.ano()); }
    }
    private static class AtualizacaoFake implements AtualizarVeiculoUseCase {
        @Override public Veiculo atualizar(AtualizarVeiculoCommand command) { return Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), command.marca(), command.modelo(), command.ano()); }
    }
    private static class ConsultaFake implements ConsultarVeiculoUseCase {
        @Override public Veiculo consultarPorId(VeiculoId veiculoId) { return Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020); }
    }
    private static class ConsultaTodosFake implements ConsultarTodosVeiculosUseCase {
        @Override public List<Veiculo> consultarTodos() { return List.of(Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020)); }
    }
    private static class ExclusaoFake implements ExcluirVeiculoUseCase {
        private VeiculoId excluido;
        @Override public void excluir(VeiculoId veiculoId) { this.excluido = veiculoId; }
    }
}