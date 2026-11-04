package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class LancamentoManualRequestDto {
    @NotNull
    private LocalDate dataReferencia;

    @NotEmpty
    @Size(max = 100)
    private List<@NotNull @Valid LancamentoManualItemRequestDto> itens;

    public LancamentoManualRequestDto() {
    }

    public LancamentoManualRequestDto(
            LocalDate dataReferencia,
            List<LancamentoManualItemRequestDto> itens) {
        this.dataReferencia = dataReferencia;
        this.itens = itens;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public List<LancamentoManualItemRequestDto> getItens() {
        return itens;
    }

    public void setItens(List<LancamentoManualItemRequestDto> itens) {
        this.itens = itens;
    }
}
