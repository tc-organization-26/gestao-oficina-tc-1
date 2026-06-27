package br.com.fiap.oficina.veiculo.domain.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import br.com.fiap.oficina.cliente.domain.model.ClienteId;
import br.com.fiap.oficina.shared.domain.DomainException;
import br.com.fiap.oficina.shared.domain.Entity;

public final class Veiculo extends Entity<VeiculoId> {

    private final VeiculoId id;
    private final ClienteId clienteId;
    private final VeiculoPlaca placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private final OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public Veiculo(
            VeiculoId id,
            ClienteId clienteId,
            VeiculoPlaca placa,
            String marca,
            String modelo,
            Integer ano,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm) {
        validarDados(clienteId, placa, marca, modelo, ano);

        if (id == null) {
            throw new DomainException("Id do veículo é obrigatório.");
        }

        this.id = id;
        this.clienteId = clienteId;
        this.placa = placa;
        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.ano = ano;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Veiculo criar(
            ClienteId clienteId,
            VeiculoPlaca placa,
            String marca,
            String modelo,
            Integer ano) {
        return new Veiculo(
                VeiculoId.novo(),
                clienteId,
                placa,
                marca,
                modelo,
                ano,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void atualizar(String marca, String modelo, Integer ano) {
        validarDados(this.clienteId, this.placa, marca, modelo, ano);

        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.ano = ano;
        this.atualizadoEm = OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static void validarDados(ClienteId clienteId, VeiculoPlaca placa, String marca, String modelo, Integer ano) {
        if (clienteId == null) {
            throw new DomainException("Cliente do veículo é obrigatório.");
        }

        if (placa == null) {
            throw new DomainException("Placa do veículo é obrigatória.");
        }

        if (marca == null || marca.isBlank()) {
            throw new DomainException("Marca do veículo é obrigatória.");
        }

        if (modelo == null || modelo.isBlank()) {
            throw new DomainException("Modelo do veículo é obrigatório.");
        }

        if (ano == null || ano <= 0) {
            throw new DomainException("Ano do veículo deve ser maior que zero.");
        }
    }

    @Override
    public VeiculoId id() {
        return id;
    }

    public ClienteId clienteId() {
        return clienteId;
    }

    public VeiculoPlaca placa() {
        return placa;
    }

    public String marca() {
        return marca;
    }

    public String modelo() {
        return modelo;
    }

    public Integer ano() {
        return ano;
    }

    public OffsetDateTime criadoEm() {
        return criadoEm;
    }

    public OffsetDateTime atualizadoEm() {
        return atualizadoEm;
    }
}
