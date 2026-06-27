package br.com.fiap.oficina.ordemservico.application.service;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.ordemservico.application.command.CriarOrdemServicoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.ConsultarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.CriarOrdemServicoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

public class OrdemServicoApplicationService implements CriarOrdemServicoUseCase, ConsultarOrdemServicoUseCase {

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
        return ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada."));
    }
}