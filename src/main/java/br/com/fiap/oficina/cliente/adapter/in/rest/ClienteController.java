package br.com.fiap.oficina.cliente.adapter.in.rest;

import br.com.fiap.oficina.cliente.adapter.in.rest.request.AtualizarClienteRequest;
import br.com.fiap.oficina.cliente.adapter.in.rest.request.CadastrarClienteRequest;
import br.com.fiap.oficina.cliente.adapter.in.rest.response.ClienteResponse;
import br.com.fiap.oficina.cliente.application.command.AtualizarClienteCommand;
import br.com.fiap.oficina.cliente.application.command.CadastrarClienteCommand;
import br.com.fiap.oficina.cliente.application.port.in.AtualizarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.CadastrarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarClienteUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ConsultarTodosClientesUseCase;
import br.com.fiap.oficina.cliente.application.port.in.ExcluirClienteUseCase;
import br.com.fiap.oficina.cliente.domain.model.ClienteId;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;
    private final ConsultarTodosClientesUseCase consultarTodosClientesUseCase;
    private final ExcluirClienteUseCase excluirClienteUseCase;

    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            ConsultarClienteUseCase consultarClienteUseCase,
            ConsultarTodosClientesUseCase consultarTodosClientesUseCase,
            ExcluirClienteUseCase excluirClienteUseCase) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
        this.consultarTodosClientesUseCase = consultarTodosClientesUseCase;
        this.excluirClienteUseCase = excluirClienteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody CadastrarClienteRequest request) {
        var command = new CadastrarClienteCommand(
                request.nome(),
                request.cpfCnpj(),
                request.email(),
                request.telefone());

        return ClienteResponse.from(cadastrarClienteUseCase.cadastrar(command));
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarClienteRequest request) {
        var command = new AtualizarClienteCommand(
                id,
                request.nome(),
                request.email(),
                request.telefone());

        return ClienteResponse.from(atualizarClienteUseCase.atualizar(command));
    }

    @GetMapping("/{id}")
    public ClienteResponse consultarPorId(@PathVariable UUID id) {
        return ClienteResponse.from(consultarClienteUseCase.consultarPorId(new ClienteId(id)));
    }

    @GetMapping
    public List<ClienteResponse> consultarTodos() {
        return consultarTodosClientesUseCase.consultarTodos().stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirClienteUseCase.excluir(new ClienteId(id));
    }
}