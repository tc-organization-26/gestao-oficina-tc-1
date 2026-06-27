package br.com.fiap.oficina.servico.adapter.in.rest;

import br.com.fiap.oficina.servico.adapter.in.rest.request.AtualizarServicoRequest;
import br.com.fiap.oficina.servico.adapter.in.rest.request.CadastrarServicoRequest;
import br.com.fiap.oficina.servico.adapter.in.rest.response.ServicoResponse;
import br.com.fiap.oficina.servico.application.command.AtualizarServicoCommand;
import br.com.fiap.oficina.servico.application.command.CadastrarServicoCommand;
import br.com.fiap.oficina.servico.application.port.in.AtualizarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.CadastrarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarServicoUseCase;
import br.com.fiap.oficina.servico.application.port.in.ConsultarTodosServicosUseCase;
import br.com.fiap.oficina.servico.application.port.in.ExcluirServicoUseCase;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
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
@RequestMapping("/servicos")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarServicoUseCase;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final ConsultarServicoUseCase consultarServicoUseCase;
    private final ConsultarTodosServicosUseCase consultarTodosServicosUseCase;
    private final ExcluirServicoUseCase excluirServicoUseCase;

    public ServicoController(CadastrarServicoUseCase cadastrarServicoUseCase,
            AtualizarServicoUseCase atualizarServicoUseCase,
            ConsultarServicoUseCase consultarServicoUseCase,
            ConsultarTodosServicosUseCase consultarTodosServicosUseCase,
            ExcluirServicoUseCase excluirServicoUseCase) {
        this.cadastrarServicoUseCase = cadastrarServicoUseCase;
        this.atualizarServicoUseCase = atualizarServicoUseCase;
        this.consultarServicoUseCase = consultarServicoUseCase;
        this.consultarTodosServicosUseCase = consultarTodosServicosUseCase;
        this.excluirServicoUseCase = excluirServicoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse cadastrar(@Valid @RequestBody CadastrarServicoRequest request) {
        var command = new CadastrarServicoCommand(
                request.codigo(),
                request.descricao(),
                request.valorUnitario(),
                request.tempoEstimadoMinutos());

        return ServicoResponse.from(cadastrarServicoUseCase.cadastrar(command));
    }

    @PutMapping("/{id}")
    public ServicoResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarServicoRequest request) {
        var command = new AtualizarServicoCommand(
                id,
                request.descricao(),
                request.valorUnitario(),
                request.tempoEstimadoMinutos());

        return ServicoResponse.from(atualizarServicoUseCase.atualizar(command));
    }

    @GetMapping("/{id}")
    public ServicoResponse consultarPorId(@PathVariable UUID id) {
        return ServicoResponse.from(consultarServicoUseCase.consultarPorId(new ServicoId(id)));
    }

    @GetMapping
    public List<ServicoResponse> consultarTodos() {
        return consultarTodosServicosUseCase.consultarTodos().stream()
                .map(ServicoResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirServicoUseCase.excluir(new ServicoId(id));
    }
}