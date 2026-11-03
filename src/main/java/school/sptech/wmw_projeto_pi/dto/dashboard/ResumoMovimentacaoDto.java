package school.sptech.wmw_projeto_pi.dto.dashboard;

import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;

public record ResumoMovimentacaoDto(
        UnidadeMedidaEnum unidadeMedida,
        BigDecimal quantidade
) {
}
