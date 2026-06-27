package br.com.fiap.oficina.cliente.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.adapter.in.rest.request.AtualizarClienteRequest;
import br.com.fiap.oficina.cliente.adapter.in.rest.request.CadastrarClienteRequest;
import br.com.fiap.oficina.cliente.application.command.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.application.command.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.application.port.in.AtualizarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.CadastrarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarTodosClientesUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ExcluirClienteUseCase;
import br.com.fiap.oficina.cliente.domain.model.Cliente;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.cliente.domain.model.CpfCnpj;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClienteControllerTest {
    @Test
    void cadastrarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.cadastrar(new CadastrarClienteRequest("Maria", "12345678901", "maria@email.com", "11"));

        assertEquals("Maria", response.nome());
    }

    @Test
    void atualizarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.atualizar(UUID.randomUUID(), new AtualizarClienteRequest("Joao", "joao@email.com", "22"));

        assertEquals("Joao", response.nome());
    }

    @Test
    void consultarRetornaResponse() {
        var controller = controller(new ExclusaoFake());

        var response = controller.consultarPorId(UUID.randomUUID());

        assertEquals("Maria", response.nome());
    }

    @Test
    void consultarTodosRetornaResponses() {
        var controller = controller(new ExclusaoFake());

        var responses = controller.consultarTodos();

        assertEquals(1, responses.size());
        assertEquals("Maria", responses.get(0).nome());
    }

    @Test
    void excluirEncaminhaIdParaUseCase() {
        var exclusao = new ExclusaoFake();
        var controller = controller(exclusao);
        var id = UUID.randomUUID();

        controller.excluir(id);

        assertEquals(id, exclusao.excluido.value());
    }

    private static ClienteController controller(ExclusaoFake exclusao) {
        return new ClienteController(new CadastroFake(), new AtualizacaoFake(), new ConsultaFake(), new ConsultaTodosFake(), exclusao);
    }

    private static class CadastroFake implements CadastrarClienteUseCase {
        @Override public Cliente cadastrar(CadastrarClienteCommand command) { return Cliente.criar(CpfCnpj.novo(command.cpfCnpj()), command.nome(), command.email(), command.telefone()); }
    }
    private static class AtualizacaoFake implements AtualizarClienteUseCase {
        @Override public Cliente atualizar(AtualizarClienteCommand command) { return Cliente.criar(CpfCnpj.novo("12345678901"), command.nome(), command.email(), command.telefone()); }
    }
    private static class ConsultaFake implements ConsultarClienteUseCase {
        @Override public Cliente consultarPorId(ClienteId clienteId) { return Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11"); }
    }
    private static class ConsultaTodosFake implements ConsultarTodosClientesUseCase {
        @Override public List<Cliente> consultarTodos() { return List.of(Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11")); }
    }
    private static class ExclusaoFake implements ExcluirClienteUseCase {
        private ClienteId excluido;
        @Override public void excluir(ClienteId clienteId) { this.excluido = clienteId; }
    }
}