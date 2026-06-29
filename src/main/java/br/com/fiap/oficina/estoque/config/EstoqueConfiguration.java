package br.com.fiap.oficina.estoque.config;

import br.com.fiap.oficina.estoque.adapter.out.persistence.jpa.EstoquePersistenceAdapter;
import br.com.fiap.oficina.estoque.adapter.out.persistence.jpa.SpringDataEstoqueRepository;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.application.service.EstoqueApplicationService;
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
