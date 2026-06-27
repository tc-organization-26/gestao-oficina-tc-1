package br.com.fiap.oficina.veiculo.adapter.in.rest;

import br.com.fiap.oficina.veiculo.adapter.in.rest.request.AtualizarVeiculoRequest;
import br.com.fiap.oficina.veiculo.adapter.in.rest.request.CadastrarVeiculoRequest;
import br.com.fiap.oficina.veiculo.adapter.in.rest.response.VeiculoResponse;
import br.com.fiap.oficina.veiculo.application.command.AtualizarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.command.CadastrarVeiculoCommand;
import br.com.fiap.oficina.veiculo.application.port.in.AtualizarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.CadastrarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarTodosVeiculosUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ConsultarVeiculoUseCase;
import br.com.fiap.oficina.veiculo.application.port.in.ExcluirVeiculoUseCase;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final ConsultarVeiculoUseCase consultarVeiculoUseCase;
    private final ConsultarTodosVeiculosUseCase consultarTodosVeiculosUseCase;
    private final ExcluirVeiculoUseCase excluirVeiculoUseCase;

    public VeiculoController(CadastrarVeiculoUseCase cadastrarVeiculoUseCase,
            AtualizarVeiculoUseCase atualizarVeiculoUseCase,
            ConsultarVeiculoUseCase consultarVeiculoUseCase,
            ConsultarTodosVeiculosUseCase consultarTodosVeiculosUseCase,
            ExcluirVeiculoUseCase excluirVeiculoUseCase) {
        this.cadastrarVeiculoUseCase = cadastrarVeiculoUseCase;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.consultarVeiculoUseCase = consultarVeiculoUseCase;
        this.consultarTodosVeiculosUseCase = consultarTodosVeiculosUseCase;
        this.excluirVeiculoUseCase = excluirVeiculoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeiculoResponse cadastrar(@Valid @RequestBody CadastrarVeiculoRequest request) {
        var command = new CadastrarVeiculoCommand(
                UUID.fromString(request.clienteId()),
                request.placa(),
                request.marca(),
                request.modelo(),
                request.ano());

        return VeiculoResponse.from(cadastrarVeiculoUseCase.cadastrar(command));
    }

    @PutMapping("/{id}")
    public VeiculoResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarVeiculoRequest request) {
        var command = new AtualizarVeiculoCommand(
                id,
                request.marca(),
                request.modelo(),
                request.ano());

        return VeiculoResponse.from(atualizarVeiculoUseCase.atualizar(command));
    }

    @GetMapping("/{id}")
    public VeiculoResponse consultarPorId(@PathVariable UUID id) {
        return VeiculoResponse.from(consultarVeiculoUseCase.consultarPorId(new VeiculoId(id)));
    }

    @GetMapping
    public List<VeiculoResponse> consultarTodos() {
        return consultarTodosVeiculosUseCase.consultarTodos().stream()
                .map(VeiculoResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirVeiculoUseCase.excluir(new VeiculoId(id));
    }
}