package br.com.fiap.oficina.estoque.config;

import br.com.fiap.oficina.estoque.adapter.out.persistence.jpa.EstoquePersistenceAdapter;
import br.com.fiap.oficina.estoque.adapter.out.persistence.jpa.SpringDataEstoqueRepository;
import br.com.fiap.oficina.estoque.application.port.in.AtualizarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.CadastrarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarTodosItensEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ExcluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.IncluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.application.service.EstoqueApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EstoqueConfiguration {

    @Bean
    public CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public ConsultarTodosItensEstoqueUseCase consultarTodosItensEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public IncluirItemEstoqueUseCase incluirItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public BaixarItemEstoqueUseCase baixarItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public ExcluirItemEstoqueUseCase excluirItemEstoqueUseCase(EstoqueRepositoryPort estoqueRepositoryPort) {
        return new EstoqueApplicationService(estoqueRepositoryPort);
    }

    @Bean
    public EstoqueRepositoryPort estoqueRepositoryPort(SpringDataEstoqueRepository springDataRepository) {
        return new EstoquePersistenceAdapter(springDataRepository);
    }
}