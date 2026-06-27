package br.com.fiap.oficina.ordemservico.adapter.in.rest;

import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.CriarOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.response.OrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;

    public OrdemServicoController(
            CriarOrdemServicoUseCase criarOrdemServicoUseCase,
            ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.consultarOrdemServicoUseCase = consultarOrdemServicoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdemServicoResponse criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        var command = new CriarOrdemServicoCommand(request.clienteId(), request.veiculoId(), request.anotacoes());
        return OrdemServicoResponse.from(criarOrdemServicoUseCase.criar(command));
    }

    @GetMapping("/{id}")
    public OrdemServicoResponse consultarPorId(@PathVariable UUID id) {
        return OrdemServicoResponse.from(consultarOrdemServicoUseCase.consultarPorId(new OrdemServicoId(id)));
    }
}