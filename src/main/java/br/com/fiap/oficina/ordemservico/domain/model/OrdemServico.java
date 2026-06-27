package br.com.fiap.oficina.ordemservico.domain.model;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.shared.domain.Entity;
import br.com.fiap.oficina.veiculo.domain.model.VeiculoId;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class OrdemServico extends Entity<OrdemServicoId> {

    private final OrdemServicoId id;
    private final Long numero;
    private final ClienteId clienteId;
    private final VeiculoId veiculoId;
    private StatusOrdemServico status;
    private String anotacoes;
    private final OffsetDateTime dataRecebimento;
    private OffsetDateTime inicioExecucaoEm;
    private OffsetDateTime finalizadaEm;
    private OffsetDateTime entregueEm;

    public OrdemServico(
            OrdemServicoId id,
            Long numero,
            ClienteId clienteId,
            VeiculoId veiculoId,
            StatusOrdemServico status,
            String anotacoes,
            OffsetDateTime dataRecebimento,
            OffsetDateTime inicioExecucaoEm,
            OffsetDateTime finalizadaEm,
            OffsetDateTime entregueEm) {
        if (id == null) {
            throw new DomainException("Id da ordem de servico e obrigatorio.");
        }
        if (clienteId == null) {
            throw new DomainException("Cliente da ordem de servico e obrigatorio.");
        }
        if (veiculoId == null) {
            throw new DomainException("Veiculo da ordem de servico e obrigatorio.");
        }
        this.id = id;
        this.numero = numero;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.status = status == null ? StatusOrdemServico.RECEBIDA : status;
        this.anotacoes = anotacoes;
        this.dataRecebimento = dataRecebimento == null ? OffsetDateTime.now(ZoneOffset.UTC) : dataRecebimento;
        this.inicioExecucaoEm = inicioExecucaoEm;
        this.finalizadaEm = finalizadaEm;
        this.entregueEm = entregueEm;
    }

    public static OrdemServico criar(ClienteId clienteId, VeiculoId veiculoId, String anotacoes) {
        return new OrdemServico(
                OrdemServicoId.novo(),
                null,
                clienteId,
                veiculoId,
                StatusOrdemServico.RECEBIDA,
                anotacoes,
                OffsetDateTime.now(ZoneOffset.UTC),
                null,
                null,
                null);
    }

    public void iniciarExecucao() {
        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.inicioExecucaoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void finalizar() {
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadaEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public void entregar() {
        this.status = StatusOrdemServico.ENTREGUE;
        this.entregueEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override public OrdemServicoId id() { return id; }
    public Long numero() { return numero; }
    public ClienteId clienteId() { return clienteId; }
    public VeiculoId veiculoId() { return veiculoId; }
    public StatusOrdemServico status() { return status; }
    public String anotacoes() { return anotacoes; }
    public OffsetDateTime dataRecebimento() { return dataRecebimento; }
    public OffsetDateTime inicioExecucaoEm() { return inicioExecucaoEm; }
    public OffsetDateTime finalizadaEm() { return finalizadaEm; }
    public OffsetDateTime entregueEm() { return entregueEm; }
}