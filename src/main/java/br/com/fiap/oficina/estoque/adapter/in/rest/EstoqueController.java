package br.com.fiap.oficina.estoque.adapter.in.rest;

import br.com.fiap.oficina.estoque.adapter.in.rest.request.AtualizarItemEstoqueRequest;
import br.com.fiap.oficina.estoque.adapter.in.rest.request.CadastrarItemEstoqueRequest;
import br.com.fiap.oficina.estoque.adapter.in.rest.request.MovimentarEstoqueRequest;
import br.com.fiap.oficina.estoque.adapter.in.rest.response.ItemEstoqueResponse;
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
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase;
    private final ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase;
    private final ConsultarTodosItensEstoqueUseCase consultarTodosItensEstoqueUseCase;
    private final AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase;
    private final IncluirItemEstoqueUseCase incluirItemEstoqueUseCase;
    private final BaixarItemEstoqueUseCase baixarItemEstoqueUseCase;
    private final ExcluirItemEstoqueUseCase excluirItemEstoqueUseCase;

    public EstoqueController(
            CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase,
            ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase,
            ConsultarTodosItensEstoqueUseCase consultarTodosItensEstoqueUseCase,
            AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase,
            IncluirItemEstoqueUseCase incluirItemEstoqueUseCase,
            BaixarItemEstoqueUseCase baixarItemEstoqueUseCase,
            ExcluirItemEstoqueUseCase excluirItemEstoqueUseCase) {
        this.cadastrarItemEstoqueUseCase = cadastrarItemEstoqueUseCase;
        this.consultarItemEstoqueUseCase = consultarItemEstoqueUseCase;
        this.consultarTodosItensEstoqueUseCase = consultarTodosItensEstoqueUseCase;
        this.atualizarItemEstoqueUseCase = atualizarItemEstoqueUseCase;
        this.incluirItemEstoqueUseCase = incluirItemEstoqueUseCase;
        this.baixarItemEstoqueUseCase = baixarItemEstoqueUseCase;
        this.excluirItemEstoqueUseCase = excluirItemEstoqueUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemEstoqueResponse cadastrar(@Valid @RequestBody CadastrarItemEstoqueRequest request) {
        var command = new CadastrarItemEstoqueCommand(
                request.codigo(), request.descricao(), request.valorUnitario(), request.quantidadeInicial());
        return ItemEstoqueResponse.from(cadastrarItemEstoqueUseCase.cadastrar(command));
    }

    @GetMapping("/{id}")
    public ItemEstoqueResponse consultarPorId(@PathVariable UUID id) {
        return ItemEstoqueResponse.from(consultarItemEstoqueUseCase.consultarPorId(new ItemEstoqueId(id)));
    }

    @GetMapping
    public List<ItemEstoqueResponse> consultarTodos() {
        return consultarTodosItensEstoqueUseCase.consultarTodos().stream()
                .map(ItemEstoqueResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    public ItemEstoqueResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarItemEstoqueRequest request) {
        var command = new AtualizarItemEstoqueCommand(id, request.descricao(), request.valorUnitario());
        return ItemEstoqueResponse.from(atualizarItemEstoqueUseCase.atualizar(command));
    }

    @PostMapping("/{id}/inclusoes")
    public ItemEstoqueResponse incluir(@PathVariable UUID id, @Valid @RequestBody MovimentarEstoqueRequest request) {
        return ItemEstoqueResponse.from(incluirItemEstoqueUseCase.incluir(new IncluirItemEstoqueCommand(id, request.quantidade())));
    }

    @PostMapping("/{id}/baixas")
    public ItemEstoqueResponse baixar(@PathVariable UUID id, @Valid @RequestBody MovimentarEstoqueRequest request) {
        return ItemEstoqueResponse.from(baixarItemEstoqueUseCase.baixar(new BaixarItemEstoqueCommand(id, request.quantidade())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirItemEstoqueUseCase.excluir(new ItemEstoqueId(id));
    }
}