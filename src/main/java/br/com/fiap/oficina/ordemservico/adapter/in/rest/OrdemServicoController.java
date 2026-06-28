package br.com.fiap.oficina.ordemservico.adapter.in.rest;

import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.CriarOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.RegistrarDiagnosticoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.FecharOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.response.CriarOrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.AdicionarItemServicoOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase;
    private final AdicionarItemServicoOrcamentoUseCase adicionarItemServicoOrcamentoUseCase;
    private final FecharOrcamentoUseCase fecharOrcamentoUseCase;

    public OrdemServicoController(
            CriarOrdemServicoUseCase criarOrdemServicoUseCase,
            ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase,
            IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase,
            AdicionarItemServicoOrcamentoUseCase adicionarItemServicoOrcamentoUseCase,
            FecharOrcamentoUseCase fecharOrcamentoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.consultarOrdemServicoUseCase = consultarOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.registrarDiagnosticoUseCase = registrarDiagnosticoUseCase;
        this.adicionarItemServicoOrcamentoUseCase = adicionarItemServicoOrcamentoUseCase;
        this.fecharOrcamentoUseCase = fecharOrcamentoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CriarOrdemServicoResponse criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        var command = new CriarOrdemServicoCommand(request.clienteId(), request.veiculoId(), request.anotacoes());
        return CriarOrdemServicoResponse.from(criarOrdemServicoUseCase.criar(command));
    }

    @GetMapping("/{id}")
    public CriarOrdemServicoResponse consultarPorId(@PathVariable UUID id) {
        return CriarOrdemServicoResponse.from(consultarOrdemServicoUseCase.consultarPorId(new OrdemServicoId(id)));
    }

    @PostMapping("/{id}/diagnostico/inicio")
    public CriarOrdemServicoResponse iniciarDiagnostico(@PathVariable UUID id) {
        return CriarOrdemServicoResponse.from(iniciarDiagnosticoUseCase.iniciarDiagnostico(new OrdemServicoId(id)));
    }

    @PostMapping("/{id}/diagnostico")
    public CriarOrdemServicoResponse registrarDiagnostico(
            @PathVariable UUID id,
            @Valid @RequestBody RegistrarDiagnosticoRequest request) {
        var command = new RegistrarDiagnosticoCommand(id, request.descricao());
        return CriarOrdemServicoResponse.from(registrarDiagnosticoUseCase.registrarDiagnostico(command));
    }

    @PostMapping("/{ordemId}/orcamento/servicos")
    public ResponseEntity<Void> adicionarItemServicoAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemServicoOrcamentoRequest request) {
        var cmd = new br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand(
                ordemId,
                request.servicoId(),
                request.quantidade());
        adicionarItemServicoOrcamentoUseCase.adicionarItemServico(cmd);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/fechar")
    public ResponseEntity<Void> fecharOrcamento(
            @PathVariable UUID ordemId,
            @RequestBody(required = false) FecharOrcamentoRequest request) {
        var command = new FecharOrcamentoCommand(ordemId);
        fecharOrcamentoUseCase.fechar(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
