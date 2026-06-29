package br.com.fiap.oficina.ordemservico.adapter.out.persistence.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico")
public class OrdemServicoJpaEntity {

    @Id
    private UUID id;

    @Column(insertable = false, updatable = false)
    private Long numero;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "status_ordem_servico", nullable = false)
    private Integer statusOrdemServico;

    @Column(columnDefinition = "text")
    private String anotacoes;

    @OneToOne(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DiagnosticoJpaEntity diagnostico;

    @Column(name = "data_recebimento", nullable = false)
    private OffsetDateTime dataRecebimento;

    @Column(name = "inicio_execucao_em")
    private OffsetDateTime inicioExecucaoEm;

    @Column(name = "finalizada_em")
    private OffsetDateTime finalizadaEm;

    @Column(name = "entregue_em")
    private OffsetDateTime entregueEm;

    @Column(name = "pago", nullable = false)
    private boolean pago = false;

    protected OrdemServicoJpaEntity() {
    }

    public OrdemServicoJpaEntity(
            UUID id,
            Long numero,
            UUID clienteId,
            UUID veiculoId,
            Integer statusOrdemServico,
            String anotacoes,
            OffsetDateTime dataRecebimento,
            OffsetDateTime inicioExecucaoEm,
            OffsetDateTime finalizadaEm,
            OffsetDateTime entregueEm,
            boolean pago) {
        this.id = id;
        this.numero = numero;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.statusOrdemServico = statusOrdemServico;
        this.anotacoes = anotacoes;
        this.dataRecebimento = dataRecebimento;
        this.inicioExecucaoEm = inicioExecucaoEm;
        this.finalizadaEm = finalizadaEm;
        this.entregueEm = entregueEm;
        this.pago = pago;
    }

    public void setDiagnostico(DiagnosticoJpaEntity diagnostico) {
        this.diagnostico = diagnostico;
    }

    public UUID getId() { return id; }
    public Long getNumero() { return numero; }
    public UUID getClienteId() { return clienteId; }
    public UUID getVeiculoId() { return veiculoId; }
    public Integer getStatusOrdemServico() { return statusOrdemServico; }
    public String getAnotacoes() { return anotacoes; }
    public DiagnosticoJpaEntity getDiagnostico() { return diagnostico; }
    public OffsetDateTime getDataRecebimento() { return dataRecebimento; }
    public OffsetDateTime getInicioExecucaoEm() { return inicioExecucaoEm; }
    public OffsetDateTime getFinalizadaEm() { return finalizadaEm; }
    public OffsetDateTime getEntregueEm() { return entregueEm; }
    public boolean isPago() { return pago; }
}
