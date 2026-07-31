package br.com.fiap.oficina.servico.application.dtos;

public record CadastrarServicoCommand (
        String codigo,
        String descricao,
        java.math.BigDecimal valorUnitario,
        Integer tempoEstimadoMinutos
) {
}
