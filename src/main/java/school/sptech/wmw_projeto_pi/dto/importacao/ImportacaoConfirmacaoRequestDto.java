package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ImportacaoConfirmacaoRequestDto {
    @NotEmpty
    @Size(max = 10000)
    private List<@NotNull @Valid ImportacaoSelecaoRequestDto> linhas;

    public ImportacaoConfirmacaoRequestDto() {
    }

    public ImportacaoConfirmacaoRequestDto(
            List<ImportacaoSelecaoRequestDto> linhas) {
        this.linhas = linhas;
    }

    public List<ImportacaoSelecaoRequestDto> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<ImportacaoSelecaoRequestDto> linhas) {
        this.linhas = linhas;
    }
}
