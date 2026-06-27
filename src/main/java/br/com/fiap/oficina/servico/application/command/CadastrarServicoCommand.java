package br.com.fiap.oficina.servico.application.command;

public record CadastrarServicoCommand (
        String codigo,
        String descricao,
        java.math.BigDecimal valorUnitario,
        Integer tempoEstimadoMinutos
) {
}
