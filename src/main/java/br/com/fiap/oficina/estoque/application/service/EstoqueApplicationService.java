package br.com.fiap.oficina.estoque.application.service;

import br.com.fiap.oficina.estoque.application.command.AtualizarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.BaixarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.CadastrarItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.command.IncluirItemEstoqueCommand;
import br.com.fiap.oficina.estoque.application.port.in.AtualizarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.BaixarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.CadastrarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarTodosItensEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarPorCodigoEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ConsultarEstoqueAtivoUseCase;
import br.com.fiap.oficina.estoque.application.port.in.ExcluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.in.IncluirItemEstoqueUseCase;
import br.com.fiap.oficina.estoque.application.port.out.EstoqueRepositoryPort;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoque;
import br.com.fiap.oficina.estoque.domain.model.ItemEstoqueId;
import br.com.fiap.oficina.shared.domain.DomainException;

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
        var item = consultarPorId(new ItemEstoqueId(command.itemEstoqueId()));
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
