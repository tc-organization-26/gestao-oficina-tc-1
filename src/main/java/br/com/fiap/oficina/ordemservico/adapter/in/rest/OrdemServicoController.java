package br.com.fiap.oficina.ordemservico.adapter.in.rest;

import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.AdicionarItemServicoOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.AdicionarItemPecaOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.BaixarEstoqueOrdemRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.CriarOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.FecharOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.request.RegistrarDiagnosticoRequest;
import br.com.fiap.oficina.ordemservico.adapter.in.rest.response.CriarOrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.BaixarEstoqueOrdemCommand;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AlterarOrcamentoDuranteExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.BaixarEstoqueOrdemServicoUseCase;
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
    private final BaixarEstoqueOrdemServicoUseCase baixarEstoqueOrdemServicoUseCase;
    private final AlterarOrcamentoDuranteExecucaoUseCase alterarOrcamentoDuranteExecucaoUseCase;
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
            BaixarEstoqueOrdemServicoUseCase baixarEstoqueOrdemServicoUseCase,
            AlterarOrcamentoDuranteExecucaoUseCase alterarOrcamentoDuranteExecucaoUseCase,
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
        this.baixarEstoqueOrdemServicoUseCase = baixarEstoqueOrdemServicoUseCase;
        this.alterarOrcamentoDuranteExecucaoUseCase = alterarOrcamentoDuranteExecucaoUseCase;
        this.finalizarExecucaoUseCase = finalizarExecucaoUseCase;
        this.registrarPagamentoUseCase = registrarPagamentoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
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

    @GetMapping
    public List<CriarOrdemServicoResponse> consultarOrdens(
            @RequestParam(required = false) StatusOrdemServico status) {
        return consultarOrdemServicoUseCase.consultarOrdens(status).stream()
                .map(CriarOrdemServicoResponse::from)
                .toList();
    }

    @GetMapping("/clientes/{clienteId}/historico-atendimentos")
    public List<CriarOrdemServicoResponse> historicoAtendimentos(@PathVariable UUID clienteId) {
        return consultarOrdemServicoUseCase.consultarHistoricoPorCliente(clienteId).stream()
                .map(CriarOrdemServicoResponse::from)
                .toList();
    }

    @GetMapping("/{id}/acompanhamento")
    public CriarOrdemServicoResponse acompanhamento(@PathVariable UUID id) {
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
        var cmd = new AdicionarItemServicoOrcamentoCommand(ordemId, request.servicoId(), request.quantidade());
        adicionarItemServicoOrcamentoUseCase.adicionarItemServico(cmd);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/orcamento/pecas")
    public ResponseEntity<Void> adicionarItemPecaAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemPecaOrcamentoRequest request) {
        var cmd = new AdicionarItemPecaOrcamentoCommand(ordemId, request.itemEstoqueId(), request.quantidade());
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
    public CriarOrdemServicoResponse aprovarOrcamento(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(aprovarOrcamentoUseCase.aprovar(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/recusa")
    public CriarOrdemServicoResponse recusarOrcamento(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(recusarOrcamentoUseCase.recusar(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/ajustes")
    public CriarOrdemServicoResponse pedirAjuste(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(pedirAjusteOrcamentoUseCase.pedirAjuste(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/alteracoes")
    public CriarOrdemServicoResponse alterarOrcamento(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(alterarOrcamentoDuranteExecucaoUseCase.alterarOrcamento(ordemId));
    }

    @PostMapping("/{ordemId}/execucao/inicio")
    public CriarOrdemServicoResponse iniciarExecucao(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(iniciarExecucaoUseCase.iniciarExecucao(ordemId));
    }

    @PostMapping("/{ordemId}/estoque/baixas")
    public ResponseEntity<Void> baixarEstoque(
            @PathVariable UUID ordemId,
            @Valid @RequestBody BaixarEstoqueOrdemRequest request) {
        baixarEstoqueOrdemServicoUseCase.baixarEstoque(
                new BaixarEstoqueOrdemCommand(ordemId, request.itemEstoqueId(), request.quantidade()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{ordemId}/execucao/finalizacao")
    public CriarOrdemServicoResponse finalizarExecucao(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(finalizarExecucaoUseCase.finalizar(ordemId));
    }

    @PostMapping("/{ordemId}/pagamento")
    public CriarOrdemServicoResponse registrarPagamento(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(registrarPagamentoUseCase.registrarPagamento(ordemId));
    }

    @PostMapping("/{ordemId}/entrega")
    public CriarOrdemServicoResponse entregar(@PathVariable UUID ordemId) {
        return CriarOrdemServicoResponse.from(entregarOrdemServicoUseCase.entregar(ordemId));
    }
}

