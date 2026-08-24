package br.com.fiap.oficina.ordemservico.frameworks.config;

import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.event.SpringDomainEventPublisherGateway;
import br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa.EstoqueVerificadorAdapter;
import br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa.OrcamentoJpaGateway;
import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.OrcamentoSpringDataRepository;
import br.com.fiap.oficina.ordemservico.interfaceadapters.gateways.persistence.jpa.OrdemServicoJpaGateway;
import br.com.fiap.oficina.ordemservico.frameworks.persistence.jpa.SpringDataOrdemServicoRepository;
import br.com.fiap.oficina.ordemservico.application.gateways.OrcamentoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.OrdemServicoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.PublicadorEventoGateway;
import br.com.fiap.oficina.ordemservico.application.gateways.VerificadorEstoqueGateway;
import br.com.fiap.oficina.ordemservico.application.usecases.interactors.OrcamentoApplicationService;
import br.com.fiap.oficina.ordemservico.application.usecases.interactors.OrdemServicoApplicationService;
import br.com.fiap.oficina.servico.application.gateways.ServicoGateway;
import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemServicoConfiguration {

    @Bean
    public OrdemServicoApplicationService ordemServicoApplicationService(
            OrdemServicoGateway ordemServicoGateway,
            OrcamentoGateway orcamentoGateway,
            ServicoGateway servicoGateway,
            EstoqueGateway estoqueGateway,
            VerificadorEstoqueGateway verificadorEstoqueGateway,
            PublicadorEventoGateway publicadorEventoGateway) {
        return new OrdemServicoApplicationService(
                ordemServicoGateway,
                orcamentoGateway,
                servicoGateway,
                estoqueGateway,
                verificadorEstoqueGateway,
                publicadorEventoGateway);
    }

    @Bean
    public OrcamentoApplicationService orcamentoApplicationService(
            OrcamentoGateway orcamentoGateway,
            OrdemServicoGateway ordemServicoGateway,
            ServicoGateway servicoGateway,
            EstoqueGateway estoqueGateway,
            VerificadorEstoqueGateway verificadorEstoqueGateway,
            PublicadorEventoGateway publicadorEventoGateway) {
        return new OrcamentoApplicationService(
                orcamentoGateway,
                ordemServicoGateway,
                servicoGateway,
                estoqueGateway,
                verificadorEstoqueGateway,
                publicadorEventoGateway);
    }

    @Bean
    public OrdemServicoGateway ordemServicoGateway(
            SpringDataOrdemServicoRepository springDataRepository,
            OrcamentoSpringDataRepository orcamentoSpringDataRepository,
            EntityManager entityManager) {
        return new OrdemServicoJpaGateway(springDataRepository, orcamentoSpringDataRepository, entityManager);
    }

    @Bean
    public OrcamentoGateway orcamentoGateway(OrcamentoSpringDataRepository springDataRepository) {
        return new OrcamentoJpaGateway(springDataRepository);
    }

    @Bean
    public VerificadorEstoqueGateway verificadorEstoqueGateway(EstoqueGateway estoqueGateway) {
        return new EstoqueVerificadorAdapter(estoqueGateway);
    }

    @Bean
    public PublicadorEventoGateway publicadorEventoGateway(ApplicationEventPublisher eventPublisher) {
        return new SpringDomainEventPublisherGateway(eventPublisher);
    }
}
