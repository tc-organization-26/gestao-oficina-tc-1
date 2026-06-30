package br.com.fiap.oficina.ordemservico.adapter.in.rest;

import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.AdicionarItemServicoOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.AdicionarItemPecaOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.CriarOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.FecharOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.RegistrarDiagnosticoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.response.OrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.EntregarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.FinalizarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.IniciarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.PedirAjusteOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RecusarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RegistrarPagamentoUseCase;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.model.StatusOrdemServico;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase;
    private final AdicionarItemServicoOrcamentoUseCase adicionarItemServicoOrcamentoUseCase;
    private final AdicionarItemPecaOrcamentoUseCase adicionarItemPecaOrcamentoUseCase;
    private final FecharOrcamentoUseCase fecharOrcamentoUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final RecusarOrcamentoUseCase recusarOrcamentoUseCase;
    private final PedirAjusteOrcamentoUseCase pedirAjusteOrcamentoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final FinalizarExecucaoUseCase finalizarExecucaoUseCase;
    private final RegistrarPagamentoUseCase registrarPagamentoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    public OrdemServicoController(
            CriarOrdemServicoUseCase criarOrdemServicoUseCase,
            ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase,
            IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase,
            AdicionarItemServicoOrcamentoUseCase adicionarItemServicoOrcamentoUseCase,
            AdicionarItemPecaOrcamentoUseCase adicionarItemPecaOrcamentoUseCase,
            FecharOrcamentoUseCase fecharOrcamentoUseCase,
            AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
            RecusarOrcamentoUseCase recusarOrcamentoUseCase,
            PedirAjusteOrcamentoUseCase pedirAjusteOrcamentoUseCase,
            IniciarExecucaoUseCase iniciarExecucaoUseCase,
            FinalizarExecucaoUseCase finalizarExecucaoUseCase,
            RegistrarPagamentoUseCase registrarPagamentoUseCase,
            EntregarOrdemServicoUseCase entregarOrdemServicoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.consultarOrdemServicoUseCase = consultarOrdemServicoUseCase;
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.registrarDiagnosticoUseCase = registrarDiagnosticoUseCase;
        this.adicionarItemServicoOrcamentoUseCase = adicionarItemServicoOrcamentoUseCase;
        this.adicionarItemPecaOrcamentoUseCase = adicionarItemPecaOrcamentoUseCase;
        this.fecharOrcamentoUseCase = fecharOrcamentoUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.recusarOrcamentoUseCase = recusarOrcamentoUseCase;
        this.pedirAjusteOrcamentoUseCase = pedirAjusteOrcamentoUseCase;
        this.iniciarExecucaoUseCase = iniciarExecucaoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.registrarPagamentoUseCase = registrarPagamentoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
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

    @GetMapping
    public List<OrdemServicoResponse> consultarOrdens(
            @RequestParam(required = false) StatusOrdemServico status,
            @RequestParam(required = false) UUID clienteId) {
        var ordens = clienteId != null
                ? consultarOrdemServicoUseCase.consultarPorCliente(clienteId)
                : consultarOrdemServicoUseCase.consultarOrdens(status);
        return ordens.stream()
                .filter(ordem -> status == null || ordem.status() == status)
                .map(OrdemServicoResponse::from)
                .toList();
    }

    @GetMapping("/{id}/acompanhamento")
    public OrdemServicoResponse acompanhamento(@PathVariable UUID id) {
        return OrdemServicoResponse.from(consultarOrdemServicoUseCase.consultarPorId(new OrdemServicoId(id)));
    }

    @PostMapping("/{id}/diagnostico/inicio")
    public OrdemServicoResponse iniciarDiagnostico(@PathVariable UUID id) {
        return OrdemServicoResponse.from(iniciarDiagnosticoUseCase.iniciarDiagnostico(new OrdemServicoId(id)));
    }

    @PostMapping("/{id}/diagnostico")
    public ResponseEntity<Void> registrarDiagnostico(
            @PathVariable UUID id,
            @Valid @RequestBody RegistrarDiagnosticoRequest request) {
        var command = new RegistrarDiagnosticoCommand(id, request.descricao());
        registrarDiagnosticoUseCase.registrarDiagnostico(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/servicos")
    public ResponseEntity<Void> adicionarItemServicoAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemServicoOrcamentoRequest request) {
        var cmd = new AdicionarItemServicoOrcamentoCommand(ordemId, request.codigo(), request.quantidade());
        adicionarItemServicoOrcamentoUseCase.adicionarItemServico(cmd);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/pecas")
    public ResponseEntity<Void> adicionarItemPecaAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemPecaOrcamentoRequest request) {
        var cmd = new AdicionarItemPecaOrcamentoCommand(ordemId, request.codigo(), request.quantidade());
        adicionarItemPecaOrcamentoUseCase.adicionarItemPeca(cmd);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/fechar")
    public ResponseEntity<Void> fecharOrcamento(
            @PathVariable UUID ordemId,
            @RequestBody(required = false) FecharOrcamentoRequest request) {
        fecharOrcamentoUseCase.fechar(new FecharOrcamentoCommand(ordemId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/aprovacao")
    public OrdemServicoResponse aprovarOrcamento(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(aprovarOrcamentoUseCase.aprovar(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/recusa")
    public OrdemServicoResponse recusarOrcamento(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(recusarOrcamentoUseCase.recusar(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/ajustes")
    public OrdemServicoResponse pedirAjuste(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(pedirAjusteOrcamentoUseCase.pedirAjuste(ordemId));
    }


    @PostMapping("/{ordemId}/execucao/inicio")
    public OrdemServicoResponse iniciarExecucao(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(iniciarExecucaoUseCase.iniciarExecucao(ordemId));
    }

    @PostMapping("/{ordemId}/execucao/finalizacao")
    public OrdemServicoResponse finalizarExecucao(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(finalizarExecucaoUseCase.finalizar(ordemId));
    }

    @PostMapping("/{ordemId}/pagamento")
    public OrdemServicoResponse registrarPagamento(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(registrarPagamentoUseCase.registrarPagamento(ordemId));
    }

    @PostMapping("/{ordemId}/entrega")
    public OrdemServicoResponse entregar(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(entregarOrdemServicoUseCase.entregar(ordemId));
    }
}
