package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class ImportacaoSelecaoRequestDto {
    @NotNull
    @Positive
    private Long linhaId;

    @NotNull
    @PositiveOrZero
    private Long versao;

    public ImportacaoSelecaoRequestDto() {
    }

    public ImportacaoSelecaoRequestDto(
            Long linhaId,
            Long versao) {
        this.linhaId = linhaId;
        this.versao = versao;
    }

    public Long getLinhaId() {
        return linhaId;
    }

    public void setLinhaId(Long linhaId) {
        this.linhaId = linhaId;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }
}
