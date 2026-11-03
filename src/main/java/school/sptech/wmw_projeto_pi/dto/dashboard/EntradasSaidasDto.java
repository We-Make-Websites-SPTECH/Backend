package school.sptech.wmw_projeto_pi.dto.dashboard;

import java.util.List;

public record EntradasSaidasDto(
        List<MovimentacaoSemanalDto> movimentacoes,
        List<ResumoMovimentacaoDto> totalEntradas,
        List<ResumoMovimentacaoDto> totalSaidas
) {
}
