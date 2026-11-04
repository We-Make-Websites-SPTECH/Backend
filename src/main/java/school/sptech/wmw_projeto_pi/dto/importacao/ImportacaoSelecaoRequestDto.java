package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ImportacaoSelecaoRequestDto {
    @NotNull
    @Positive
    private Long linhaId;

    public ImportacaoSelecaoRequestDto() {
    }

    public ImportacaoSelecaoRequestDto(Long linhaId) {
        this.linhaId = linhaId;
    }

    public Long getLinhaId() {
        return linhaId;
    }

    public void setLinhaId(Long linhaId) {
        this.linhaId = linhaId;
    }
}
