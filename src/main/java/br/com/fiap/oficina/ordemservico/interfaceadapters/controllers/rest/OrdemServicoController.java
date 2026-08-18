package br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest;

import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.AdicionarItemServicoOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.AdicionarItemPecaOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.AtualizarStatusOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.CriarOrdemServicoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.FecharOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.NotificarAprovacaoOrcamentoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.controllers.rest.request.RegistrarDiagnosticoRequest;
import br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response.OrdemServicoCriadaResponse;
import br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response.OrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response.StatusOrdemServicoResponse;
import br.com.fiap.oficina.ordemservico.interfaceadapters.presenters.rest.response.TempoMedioExecucaoResponse;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.AdicionarItemPecaOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.NotificarAprovacaoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.dtos.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AdicionarItemPecaOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AprovarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.AtualizarStatusOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.EntregarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.FinalizarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.IniciarExecucaoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.NotificarAprovacaoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.PedirAjusteOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RecusarOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.usecases.RegistrarPagamentoUseCase;
import br.com.fiap.oficina.ordemservico.domain.valueobjects.OrdemServicoId;
import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private final AtualizarStatusOrdemServicoUseCase atualizarStatusOrdemServicoUseCase;
    private final NotificarAprovacaoOrcamentoUseCase notificarAprovacaoOrcamentoUseCase;

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
            EntregarOrdemServicoUseCase entregarOrdemServicoUseCase,
            AtualizarStatusOrdemServicoUseCase atualizarStatusOrdemServicoUseCase,
            NotificarAprovacaoOrcamentoUseCase notificarAprovacaoOrcamentoUseCase) {
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
        this.atualizarStatusOrdemServicoUseCase = atualizarStatusOrdemServicoUseCase;
        this.notificarAprovacaoOrcamentoUseCase = notificarAprovacaoOrcamentoUseCase;
    }

    @PostMapping
    public ResponseEntity<OrdemServicoCriadaResponse> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        var servicos = request.servicos().stream()
                .map(item -> new CriarOrdemServicoCommand.ItemServicoCommand(item.codigo(), item.quantidade()))
                .toList();
        var pecas = request.pecas().stream()
                .map(item -> new CriarOrdemServicoCommand.ItemPecaCommand(item.codigo(), item.quantidade()))
                .toList();
        var command = new CriarOrdemServicoCommand(request.clienteId(), request.veiculoId(), servicos, pecas, request.anotacoes());
        var ordemServico = criarOrdemServicoUseCase.criar(command);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ordemServico.id().value())
                .toUri();
        return ResponseEntity.created(location).body(OrdemServicoCriadaResponse.from(ordemServico));
    }

    @GetMapping("/tempo-medio-execucao")
    public TempoMedioExecucaoResponse tempoMedioExecucao() {
        return new TempoMedioExecucaoResponse(consultarOrdemServicoUseCase.consultarTempoMedioExecucao());
    }

    @GetMapping("/{id}")
    public OrdemServicoResponse consultarPorId(@PathVariable UUID id) {
        return OrdemServicoResponse.from(consultarOrdemServicoUseCase.consultarPorId(new OrdemServicoId(id)));
    }

    @GetMapping("/{id}/status")
    public StatusOrdemServicoResponse consultarStatus(@PathVariable UUID id) {
        return StatusOrdemServicoResponse.from(consultarOrdemServicoUseCase.consultarPorId(new OrdemServicoId(id)));
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
    public OrdemServicoResponse registrarDiagnostico(
            @PathVariable UUID id,
            @Valid @RequestBody RegistrarDiagnosticoRequest request) {
        var command = new RegistrarDiagnosticoCommand(id, request.descricao());
        return OrdemServicoResponse.from(registrarDiagnosticoUseCase.registrarDiagnostico(command));
    }

    @PostMapping("/{ordemId}/orcamento/servicos")
    public OrdemServicoResponse adicionarItemServicoAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemServicoOrcamentoRequest request) {
        var cmd = new AdicionarItemServicoOrcamentoCommand(ordemId, request.codigo(), request.quantidade());
        return OrdemServicoResponse.from(adicionarItemServicoOrcamentoUseCase.adicionarItemServico(cmd));
    }

    @PostMapping("/{ordemId}/orcamento/pecas")
    public OrdemServicoResponse adicionarItemPecaAoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AdicionarItemPecaOrcamentoRequest request) {
        var cmd = new AdicionarItemPecaOrcamentoCommand(ordemId, request.codigo(), request.quantidade());
        return OrdemServicoResponse.from(adicionarItemPecaOrcamentoUseCase.adicionarItemPeca(cmd));
    }

    @PostMapping("/{ordemId}/orcamento/fechar")
    public OrdemServicoResponse fecharOrcamento(
            @PathVariable UUID ordemId,
            @RequestBody(required = false) FecharOrcamentoRequest request) {
        return OrdemServicoResponse.from(fecharOrcamentoUseCase.fechar(new FecharOrcamentoCommand(ordemId)));
    }

    @PostMapping("/{ordemId}/orcamento/aprovacao")
    public OrdemServicoResponse aprovarOrcamento(@PathVariable UUID ordemId) {
        return OrdemServicoResponse.from(aprovarOrcamentoUseCase.aprovar(ordemId));
    }

    @PostMapping("/{ordemId}/orcamento/notificacoes-aprovacao")
    public StatusOrdemServicoResponse notificarAprovacaoOrcamento(
            @PathVariable UUID ordemId,
            @Valid @RequestBody NotificarAprovacaoOrcamentoRequest request) {
        var command = new NotificarAprovacaoOrcamentoCommand(
                ordemId,
                NotificarAprovacaoOrcamentoCommand.DecisaoOrcamento.valueOf(request.decisao().name()),
                request.origem(),
                request.protocoloExterno());
        var ordemServico = notificarAprovacaoOrcamentoUseCase.notificarAprovacao(command);
        return StatusOrdemServicoResponse.from(ordemServico);
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

    @PatchMapping("/{ordemId}/status")
    public StatusOrdemServicoResponse atualizarStatus(
            @PathVariable UUID ordemId,
            @Valid @RequestBody AtualizarStatusOrdemServicoRequest request) {
        return StatusOrdemServicoResponse.from(atualizarStatusOrdemServicoUseCase.atualizarStatus(ordemId, request.status()));
    }
}
