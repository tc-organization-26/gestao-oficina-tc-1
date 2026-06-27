package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServico;
import br.com.fiap.oficina.ordemservico.domain.model.OrdemServicoId;

import java.util.Optional;

public class OrdemServicoPersistenceAdapter implements OrdemServicoRepositoryPort {

    private final SpringDataOrdemServicoRepository repository;
    private final OrdemServicoJpaMapper mapper = new OrdemServicoJpaMapper();

    public OrdemServicoPersistenceAdapter(SpringDataOrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        return mapper.toDomain(repository.save(mapper.toEntity(ordemServico)));
    }

    @Override
    public Optional<OrdemServico> buscarPorId(OrdemServicoId ordemServicoId) {
        return repository.findById(ordemServicoId.value()).map(mapper::toDomain);
    }
}