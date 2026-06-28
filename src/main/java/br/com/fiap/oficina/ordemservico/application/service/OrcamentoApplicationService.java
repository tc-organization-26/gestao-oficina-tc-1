package br.com.fiap.oficina.ordemservico.application.service;

import br.com.fiap.oficina.ordemservico.application.command.AdicionarItemServicoOrcamentoCommand;
import br.com.fiap.oficina.ordemservico.application.port.in.AdicionarItemServicoOrcamentoUseCase;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.Orcamento;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoId;
import br.com.fiap.oficina.ordemservico.domain.model.OrcamentoItemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;
import br.com.fiap.oficina.servico.domain.model.ServicoId;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrcamentoApplicationService implements AdicionarItemServicoOrcamentoUseCase {

    private final OrcamentoRepositoryPort orcamentoRepository;

    public OrcamentoApplicationService(OrcamentoRepositoryPort orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    @Override
    public Orcamento adicionarItemServico(AdicionarItemServicoOrcamentoCommand command) {
        var orcamentoId = OrcamentoId.from(command.orcamentoId());
        var orcamento = orcamentoRepository.buscarPorId(orcamentoId)
            .orElseGet(() -> new Orcamento(command.orcamentoId(), OrdemServicoId.novo()));
        var item = new OrcamentoItemServico(new ServicoId(command.servicoId()), command.quantidade());
        orcamento.adicionarItemServico(item);
        return orcamentoRepository.salvar(orcamento);
    }
}
