package school.sptech.wmw_projeto_pi.dto.dashboard;

public record InsumosStatusDto(
        Long ativos,
        Long inativos,
        Long total,
        Double percentualAtivos,
        Double percentualInativos
) {
}
