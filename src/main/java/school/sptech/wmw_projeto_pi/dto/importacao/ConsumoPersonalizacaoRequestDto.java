package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ConsumoPersonalizacaoRequestDto {
    @NotNull
    @Size(max = 100)
    private List<@NotNull @Valid ConsumoVariacaoRequestDto> variacoes;

    public ConsumoPersonalizacaoRequestDto() {
    }

    public ConsumoPersonalizacaoRequestDto(
            List<ConsumoVariacaoRequestDto> variacoes) {
        this.variacoes = variacoes;
    }

    public List<ConsumoVariacaoRequestDto> getVariacoes() {
        return variacoes;
    }

    public void setVariacoes(List<ConsumoVariacaoRequestDto> variacoes) {
        this.variacoes = variacoes;
    }
}
