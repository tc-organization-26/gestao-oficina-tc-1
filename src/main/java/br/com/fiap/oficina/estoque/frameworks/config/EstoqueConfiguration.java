package br.com.fiap.oficina.estoque.frameworks.config;

import br.com.fiap.oficina.estoque.interfaceadapters.gateways.persistence.jpa.EstoquePersistenceAdapter;
import br.com.fiap.oficina.estoque.frameworks.persistence.jpa.SpringDataEstoqueRepository;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.application.usecases.interactors.EstoqueApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstoqueConfiguration {

    @Bean
    public EstoqueApplicationService estoqueApplicationService(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public EstoqueRepositoryPort estoqueRepositoryPort(SpringDataEstoqueRepository springDataRepository) {
        return new EstoquePersistenceAdapter(springDataRepository);
    }
}
