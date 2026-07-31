package br.com.fiap.oficina.estoque.application.usecases.interactors;

import br.com.fiap.oficina.estoque.application.dtos.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.dtos.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.dtos.CadastrarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.dtos.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.usecases.AtualizarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.CadastrarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.ConsultarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.ConsultarTodosItensEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.ConsultarPorCodigoEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.ConsultarEstoqueAtivoUseCase;
import br.com.fiap.oficina.estoque.application.usecases.ExcluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.usecases.IncluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.gateways.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.domain.entities.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.valueobjects.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.exceptions.DomainException;

import java.util.List;

public class EstoqueApplicationService implements CadastrarItemEstoqueUseCase,
        ConsultarItemEstoqueUseCase,
        ConsultarTodosItensEstoqueUseCase,
        ConsultarPorCodigoEstoqueUseCase,
        ConsultarEstoqueAtivoUseCase,
        AtualizarItemEstoqueUseCase,
        IncluirItemEstoqueUseCase,
        BaixarItemEstoqueUseCase,
        ExcluirItemEstoqueUseCase {

    private final EstoqueRepositoryPort estoqueRepository;

    public EstoqueApplicationService(EstoqueRepositoryPort estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public ItemEstoque cadastrar(CadastrarItemEstoqueCommand command) {
        if (estoqueRepository.existePorCodigo(command.codigo())) {
            throw new DomainException("Codigo de item de estoque ja cadastrado.");
        }
        var item = ItemEstoque.criar(command.codigo(), command.descricao(), command.valorUnitario(), command.quantidadeInicial());
        return estoqueRepository.salvar(item);
    }

    @Override
    public ItemEstoque consultarPorId(ItemEstoqueId itemEstoqueId) {
        return estoqueRepository.buscarPorId(itemEstoqueId)
                .orElseThrow(() -> new DomainException("Item de estoque nao encontrado."));
    }

    @Override
    public List<ItemEstoque> consultarTodos() {
        return estoqueRepository.buscarTodos();
    }

    @Override
    public ItemEstoque consultarPorCodigo(String codigo) {
        return estoqueRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new DomainException("Item de estoque nao encontrado com codigo: " + codigo));
    }

    @Override
    public List<ItemEstoque> consultarAtivos() {
        return estoqueRepository.buscarTodosAtivos();
    }

    @Override
    public ItemEstoque atualizar(AtualizarItemEstoqueCommand command) {
        var item = consultarPorId(new ItemEstoqueId(command.itemEstoqueId()));
        item.atualizar(command.descricao(), command.valorUnitario());
        return estoqueRepository.salvar(item);
    }

    @Override
    public ItemEstoque incluir(IncluirItemEstoqueCommand command) {
        var item = consultarPorId(new ItemEstoqueId(command.itemEstoqueId()));
        item.incluir(command.quantidade());
        return estoqueRepository.salvar(item);
    }

    @Override
    public ItemEstoque baixar(BaixarItemEstoqueCommand command) {
        var item = consultarPorCodigo(command.codigo());
        item.baixar(command.quantidade());
        return estoqueRepository.salvar(item);
    }

    @Override
    public void excluir(ItemEstoqueId itemEstoqueId) {
        var item = consultarPorId(itemEstoqueId);
        item.desativar();
        estoqueRepository.salvar(item);
    }
}
