package school.sptech.wmw_projeto_pi.dto.dashboard;

import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;

public record MovimentacaoSemanalDto(
        String semana,
        TipoMovimentacaoEnum tipo,
        UnidadeMedidaEnum unidadeMedida,
        BigDecimal quantidade
) {
}
