package br.com.fiap.oficina.ordemservico.domain.valueobjects;

import br.com.fiap.oficina.ordemservico.domain.enums.StatusOrdemServico;
import java.util.UUID;

/** A persisted status transition; null duration means its start time is unknown. */
public record TransicaoStatusOrdemServico(UUID ordemId, StatusOrdemServico anterior,
                                        StatusOrdemServico atual, Long duracaoAnteriorMs) { }
