package br.com.fiap.oficina.ordemservico.config;

import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.ordemservico.adapter.out.event.SpringDomainEventPublisherAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.BaixaEstoqueAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.EstoqueVerificadorAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.OrcamentoPersistenceAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.OrcamentoSpringDataRepository;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.OrdemServicoPersistenceAdapter;
import br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa.SpringDataOrdemServicoRepository;
import br.com.fiap.oficina.ordemservico.application.port.out.BaixaEstoquePort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrcamentoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.OrdemServicoRepositoryPort;
import br.com.fiap.oficina.ordemservico.application.port.out.PublicarEventoPort;
import br.com.fiap.oficina.ordemservico.application.port.out.VerificadorEstoquePort;
import br.com.fiap.oficina.ordemservico.application.service.OrcamentoApplicationService;
import br.com.fiap.oficina.ordemservico.application.service.OrdemServicoApplicationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemServicoConfiguration {

    @Bean
    public OrdemServicoApplicationService ordemServicoApplicationService(
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            OrcamentoRepositoryPort orcamentoRepositoryPort,
            BaixaEstoquePort baixaEstoquePort,
            PublicarEventoPort publicarEventoPort) {
        return new OrdemServicoApplicationService(
                ordemServicoRepositoryPort,
                orcamentoRepositoryPort,
                baixaEstoquePort,
                publicarEventoPort);
    }

    @Bean
    public OrcamentoApplicationService orcamentoApplicationService(
            OrcamentoRepositoryPort orcamentoRepositoryPort,
            OrdemServicoRepositoryPort ordemServicoRepositoryPort,
            VerificadorEstoquePort verificadorEstoquePort,
            PublicarEventoPort publicarEventoPort) {
        return new OrcamentoApplicationService(
                orcamentoRepositoryPort,
                ordemServicoRepositoryPort,
                verificadorEstoquePort,
                publicarEventoPort);
    }

    @Bean
    public OrdemServicoRepositoryPort ordemServicoRepositoryPort(SpringDataOrdemServicoRepository springDataRepository) {
        return new OrdemServicoPersistenceAdapter(springDataRepository);
    }

    @Bean
    public OrcamentoRepositoryPort orcamentoRepositoryPort(OrcamentoSpringDataRepository springDataRepository) {
        return new OrcamentoPersistenceAdapter(springDataRepository);
    }

    @Bean
    public BaixaEstoquePort baixaEstoquePort(BaixarItemEstoqueUseCase baixarItemEstoqueUseCase) {
        return new BaixaEstoqueAdapter(baixarItemEstoqueUseCase);
    }

    @Bean
    public VerificadorEstoquePort verificadorEstoquePort(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueVerificadorAdapter(estoqueRepositoryPort);
    }

    @Bean
    public PublicarEventoPort publicarEventoPort(ApplicationEventPublisher eventPublisher) {
        return new SpringDomainEventPublisherAdapter(eventPublisher);
    }
}
