package br.com.fiap.oficina.estoque.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.fiap.oficina.estoque.adapter.out.persistence.jpa.SpringDataEstoqueRepository;
import br.com.fiap.oficina.estoque.application.port.in.AtualizarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.CadastrarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarTodosItensEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ExcluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.IncluirItemEstoqueUseCase;
import org.junit.jupiter.api.Test;

class EstoqueConfigurationTest {
    @Test
    void criaBeansDeUseCasesEPortaSaida() {
        var configuration = new EstoqueConfiguration();
        var repositoryPort = configuration.estoqueRepositoryPort(mock(SpringDataEstoqueRepository.class));

        assertNotNull(repositoryPort);
        assertInstanceOf(CadastrarItemEstoqueUseCase.class, configuration.cadastrarItemEstoqueUseCase(repositoryPort));
        assertInstanceOf(ConsultarItemEstoqueUseCase.class, configuration.consultarItemEstoqueUseCase(repositoryPort));
        assertInstanceOf(ConsultarTodosItensEstoqueUseCase.class, configuration.consultarTodosItensEstoqueUseCase(repositoryPort));
        assertInstanceOf(AtualizarItemEstoqueUseCase.class, configuration.atualizarItemEstoqueUseCase(repositoryPort));
        assertInstanceOf(IncluirItemEstoqueUseCase.class, configuration.incluirItemEstoqueUseCase(repositoryPort));
        assertInstanceOf(BaixarItemEstoqueUseCase.class, configuration.baixarItemEstoqueUseCase(repositoryPort));
        assertInstanceOf(ExcluirItemEstoqueUseCase.class, configuration.excluirItemEstoqueUseCase(repositoryPort));
    }
}