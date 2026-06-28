package br.com.fiap.oficina.ordemservico.application.service;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.command.RegistrarDiagnosticoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.IniciarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.RegistrarDiagnosticoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.Diagnostico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public class OrdemServicoApplicationService implements CriarOrdemServicoUseCase, ConsultarOrdemServicoUseCase,
        IniciarDiagnosticoUseCase, RegistrarDiagnosticoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public OrdemServicoApplicationService(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public OrdemServico criar(CriarOrdemServicoCommand command) {
        var ordemServico = OrdemServico.criar(
                new ClienteId(command.clienteId()),
                new VeiculoId(command.veiculoId()),
                command.anotacoes());
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico consultarPorId(OrdemServicoId ordemServicoId) {
        return buscarOrdemServico(ordemServicoId);
    }

    @Override
    public OrdemServico iniciarDiagnostico(OrdemServicoId ordemServicoId) {
        var ordemServico = buscarOrdemServico(ordemServicoId);
        ordemServico.iniciarDiagnostico();
        return ordemServicoRepository.salvar(ordemServico);
    }

    @Override
    public OrdemServico registrarDiagnostico(RegistrarDiagnosticoCommand command) {
        var ordemServico = buscarOrdemServico(new OrdemServicoId(command.ordemServicoId()));
        ordemServico.registrarDiagnostico(Diagnostico.registrar(command.descricao()));
        return ordemServicoRepository.salvar(ordemServico);
    }

    private OrdemServico buscarOrdemServico(OrdemServicoId ordemServicoId) {
        return ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada."));
    }
}
