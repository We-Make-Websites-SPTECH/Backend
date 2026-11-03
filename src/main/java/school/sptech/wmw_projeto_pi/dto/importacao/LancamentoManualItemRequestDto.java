package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class LancamentoManualItemRequestDto {
    @NotNull
    @Positive
    private Integer produtoId;

    @NotNull
    @DecimalMin("1")
    @DecimalMax("2147483647")
    @Digits(integer = 10, fraction = 0)
    private BigDecimal quantidade;

    public LancamentoManualItemRequestDto() {
    }

    public LancamentoManualItemRequestDto(
            Integer produtoId,
            BigDecimal quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}
