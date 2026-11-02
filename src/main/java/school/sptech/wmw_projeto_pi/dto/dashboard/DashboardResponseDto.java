package school.sptech.wmw_projeto_pi.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponseDto(
        LocalDate dataInicio,
        LocalDate dataFim,
        List<InsumosMaisConsumidosDto> insumosMaisConsumidos,
        EntradasSaidasDto entradasSaidas,
        List<PerdasQuebrasDto> perdasQuebras,
        InsumosStatusDto insumosStatus
) {
}
