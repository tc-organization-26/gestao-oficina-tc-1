package br.com.fiap.oficina.veiculo.application.usecases.interactors;

import static org.junit.jupiter.api.Assertions.*;

import br.com.fiap.oficina.cliente.domain.valueobjects.ClienteId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;
import br.com.fiap.oficina.veiculo.application.dtos.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.dtos.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.gateways.VeiculoRepositoryPort;
import br.com.fiap.oficina.veiculo.domain.entities.Veiculo;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoId;
import br.com.fiap.oficina.veiculo.domain.valueobjects.VeiculoPlaca;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VeiculoApplicationServiceTest {
    @Test
    void cadastrarSalvaVeiculoNovo() {
        var repository = new FakeRepository(false, Optional.empty());
        var service = new VeiculoApplicationService(repository);

        var veiculo = service.cadastrar(new CadastrarVeiculoCommand(UUID.randomUUID(), "ABC1D23", "Toyota", "Corolla", 2020));

        assertNotNull(veiculo.id());
        assertSame(veiculo, repository.salvo);
    }

    @Test
    void cadastrarRejeitaPlacaDuplicada() {
        var service = new VeiculoApplicationService(new FakeRepository(true, Optional.empty()));

        assertThrows(DomainException.class,
                () -> service.cadastrar(new CadastrarVeiculoCommand(UUID.randomUUID(), "ABC1D23", "Toyota", "Corolla", 2020)));
    }
    @Test
    void cadastrarConsultaDuplicidadeComPlacaNormalizada() {
        var repository = new FakeRepository(false, Optional.empty());
        var service = new VeiculoApplicationService(repository);

        var veiculo = service.cadastrar(new CadastrarVeiculoCommand(UUID.randomUUID(), "abc-1d23", "Toyota", "Corolla", 2020));

        assertEquals("ABC1D23", repository.placaConsultada);
        assertEquals("ABC1D23", veiculo.placa().value());
    }

    @Test
    void atualizarBuscaAlteraESalva() {
        var existente = Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);
        var service = new VeiculoApplicationService(new FakeRepository(false, Optional.of(existente)));

        var atualizado = service.atualizar(new AtualizarVeiculoCommand(existente.id().value(), "Honda", "Civic", 2021));

        assertEquals("Honda", atualizado.marca());
    }

    @Test
    void consultarRejeitaVeiculoInexistente() {
        var service = new VeiculoApplicationService(new FakeRepository(false, Optional.empty()));

        assertThrows(DomainException.class, () -> service.consultarPorId(new VeiculoId(UUID.randomUUID())));
    }

    @Test
    void consultarTodosRetornaVeiculosDoRepositorio() {
        var existente = Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);
        var service = new VeiculoApplicationService(new FakeRepository(false, Optional.of(existente), List.of(existente)));

        var veiculos = service.consultarTodos();

        assertEquals(List.of(existente), veiculos);
    }

    @Test
    void excluirValidaExistenciaEExcluiNoRepositorio() {
        var existente = Veiculo.criar(ClienteId.novo(), VeiculoPlaca.novo("ABC1D23"), "Toyota", "Corolla", 2020);
        var repository = new FakeRepository(false, Optional.of(existente));
        var service = new VeiculoApplicationService(repository);

        service.excluir(existente.id());

        assertEquals(existente.id(), repository.excluido);
    }

    private static class FakeRepository implements VeiculoRepositoryPort {
        private final boolean existe;
        private final Optional<Veiculo> busca;
        private final List<Veiculo> todos;
        private Veiculo salvo;
        private VeiculoId excluido;
        private String placaConsultada;

        FakeRepository(boolean existe, Optional<Veiculo> busca) {
            this(existe, busca, List.of());
        }

        FakeRepository(boolean existe, Optional<Veiculo> busca, List<Veiculo> todos) {
            this.existe = existe;
            this.busca = busca;
            this.todos = todos;
        }

        @Override public boolean existePorPlaca(String placa) { this.placaConsultada = placa; return existe; }
        @Override public Veiculo salvar(Veiculo veiculo) { this.salvo = veiculo; return veiculo; }
        @Override public Optional<Veiculo> buscarPorId(VeiculoId veiculoId) { return busca; }
        @Override public List<Veiculo> buscarTodos() { return todos; }
        @Override public void excluirPorId(VeiculoId veiculoId) { this.excluido = veiculoId; }
        @Override public List<Veiculo> buscarPorClienteId(ClienteId clienteId) { return todos; }
    }
}