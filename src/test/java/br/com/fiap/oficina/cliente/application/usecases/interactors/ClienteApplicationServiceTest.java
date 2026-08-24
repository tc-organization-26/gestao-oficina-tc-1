package br.com.fiap.oficina.cliente.application.usecases.interactors;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.application.dtos.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.application.dtos.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.application.gateways.ClienteGateway;
import br.com.fiap.oficina.cliente.domain.entities.Cliente;
import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.cliente.domain.valueobjects.CpfCnpj;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClienteApplicationServiceTest {
    @Test
    void cadastrarSalvaClienteNovo() {
        var repository = new FakeRepository(false, Optional.empty());
        var service = new ClienteApplicationService(repository);

        var cliente = service.cadastrar(new CadastrarClienteCommand("Maria", "12345678901", "maria@email.com", "11"));

        assertNotNull(cliente.id());
        assertSame(cliente, repository.salvo);
    }

    @Test
    void cadastrarRejeitaCpfCnpjDuplicado() {
        var service = new ClienteApplicationService(new FakeRepository(true, Optional.empty()));

        assertThrows(DomainException.class,
                () -> service.cadastrar(new CadastrarClienteCommand("Maria", "12345678901", "maria@email.com", "11")));
    }

    @Test
    void atualizarBuscaAlteraESalva() {
        var existente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11");
        var service = new ClienteApplicationService(new FakeRepository(false, Optional.of(existente)));

        var atualizado = service.atualizar(new AtualizarClienteCommand(existente.id().value(), "Joao", "joao@email.com", "22"));

        assertEquals("Joao", atualizado.nome());
    }

    @Test
    void consultarRejeitaClienteInexistente() {
        var service = new ClienteApplicationService(new FakeRepository(false, Optional.empty()));

        assertThrows(DomainException.class, () -> service.consultarPorId(new ClienteId(UUID.randomUUID())));
    }

    @Test
    void consultarTodosRetornaClientesDoRepositorio() {
        var existente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11");
        var service = new ClienteApplicationService(new FakeRepository(false, Optional.of(existente), List.of(existente)));

        var clientes = service.consultarTodos();

        assertEquals(List.of(existente), clientes);
    }

    @Test
    void excluirValidaExistenciaEExcluiNoRepositorio() {
        var existente = Cliente.criar(CpfCnpj.novo("12345678901"), "Maria", "maria@email.com", "11");
        var repository = new FakeRepository(false, Optional.of(existente));
        var service = new ClienteApplicationService(repository);

        service.excluir(existente.id());

        assertEquals(existente.id(), repository.excluido);
    }

    private static class FakeRepository implements ClienteGateway {
        private final boolean existe;
        private final Optional<Cliente> busca;
        private final List<Cliente> todos;
        private Cliente salvo;
        private ClienteId excluido;

        FakeRepository(boolean existe, Optional<Cliente> busca) {
            this(existe, busca, List.of());
        }

        FakeRepository(boolean existe, Optional<Cliente> busca, List<Cliente> todos) {
            this.existe = existe;
            this.busca = busca;
            this.todos = todos;
        }

        @Override public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) { return existe; }
        @Override public Cliente salvar(Cliente cliente) { this.salvo = cliente; return cliente; }
        @Override public Optional<Cliente> buscarPorId(ClienteId clienteId) { return busca; }
        @Override public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) { return busca; }
        @Override public List<Cliente> buscarTodos() { return todos; }
        @Override public void excluirPorId(ClienteId clienteId) { this.excluido = clienteId; }
    }
}