package br.com.fiap.oficina.ordemservico.domain.enums;

public enum StatusOrdemServico {
    RECEBIDA("Recebida", 4, true),
    EM_DIAGNOSTICO("Diagnostico", 3, true),
    AGUARDANDO_APROVACAO("Aguardando Aprovacao", 2, true),
    EM_EXECUCAO("Execucao", 1, true),
    FINALIZADA("Finalizada", 5, false),
    ENTREGUE("Entregue", 6, false);

    private final String descricao;
    private final int prioridadeListagem;
    private final boolean ativaNaOficina;

    StatusOrdemServico(String descricao, int prioridadeListagem, boolean ativaNaOficina) {
        this.descricao = descricao;
        this.prioridadeListagem = prioridadeListagem;
        this.ativaNaOficina = ativaNaOficina;
    }

    public String descricao() {
        return descricao;
    }

    public int prioridadeListagem() {
        return prioridadeListagem;
    }

    public boolean ativaNaOficina() {
        return ativaNaOficina;
    }
}
