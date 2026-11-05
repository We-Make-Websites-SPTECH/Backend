package school.sptech.wmw_projeto_pi.dto.interno;

import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Resultado interno da consulta de movimentações do período.
 * Não é exposto diretamente pela API.
 */
public record MovimentacaoPeriodoDto(
        Integer insumoId,
        String nome,
        UnidadeMedidaEnum unidadeMedida,
        Boolean ativo,
        TipoMovimentacaoEnum tipo,
        MotivoMovimentacaoEnum motivo,
        BigDecimal quantidade,
        LocalDateTime dataHora
) {
}
