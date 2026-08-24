package br.com.fiap.oficina.estoque.frameworks.config;

import br.com.fiap.oficina.estoque.interfaceadapters.gateways.persistence.jpa.EstoqueJpaGateway;
import br.com.fiap.oficina.estoque.frameworks.persistence.jpa.SpringDataEstoqueRepository;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueGateway;
import br.com.fiap.oficina.estoque.application.usecases.interactors.EstoqueApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstoqueConfiguration {

    @Bean
    public EstoqueApplicationService estoqueApplicationService(EstoqueGateway estoqueGateway) {
        return new EstoqueApplicationService(estoqueGateway);
    }

    @Bean
    public EstoqueGateway estoqueGateway(SpringDataEstoqueRepository springDataRepository) {
        return new EstoqueJpaGateway(springDataRepository);
    }
}
