package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ConsumoPersonalizacaoRequestDto {
    @NotNull
    @PositiveOrZero
    private Long versao;

    @NotNull
    @Size(max = 100)
    private List<@NotNull @Valid ConsumoVariacaoRequestDto> variacoes;

    public ConsumoPersonalizacaoRequestDto() {
    }

    public ConsumoPersonalizacaoRequestDto(
            Long versao,
            List<ConsumoVariacaoRequestDto> variacoes) {
        this.versao = versao;
        this.variacoes = variacoes;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public List<ConsumoVariacaoRequestDto> getVariacoes() {
        return variacoes;
    }

    public void setVariacoes(List<ConsumoVariacaoRequestDto> variacoes) {
        this.variacoes = variacoes;
    }
}
