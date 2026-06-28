package br.com.fiap.oficina.ordemservico.application.service;

import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.command.FecharOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.in.FecharOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import br.com.fiap.oficina.shared.domain.DomainException;
import org.springframework.stereotype.Service;

@Service
public class OrcamentoApplicationService implements AdicionarItemServicoOrcamentoUseCase, FecharOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepository;
    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public OrcamentoApplicationService(
            OrcamentoRepositoryPort orcamentoRepository,
            OrdemServicoRepositoryPort ordemServicoRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command) {
        var orcamentoId = OrcamentoId.from(command.orcamentoId());
        var ordemServicoId = new OrdemServicoId(command.orcamentoId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
                .orElseGet(() -> new Orcamento(command.orcamentoId(), ordemServicoId));
        var item = new OrcamentoItemServico(new ServicoId(command.servicoId()), command.quantidade());
        orcamento.adicionarItemServico(item);
        return orcamentoRepository.salvar(orcamento);
    }

    @Override
    public void fechar(FecharOrcamentoCommand command) {
        var ordemServicoId = new OrdemServicoId(command.ordemId());
        var ordemServico = ordemServicoRepository.buscarPorId(ordemServicoId)
                .orElseThrow(() -> new DomainException("Ordem de servico nao encontrada: " + command.ordemId()));

        var orcamentoId = OrcamentoId.from(command.ordemId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new DomainException("Orcamento nao encontrado: " + command.ordemId()));

        orcamento.fechar();

        ordemServico.finalizarOrcamento();
        ordemServicoRepository.salvar(ordemServico);
    }
}
