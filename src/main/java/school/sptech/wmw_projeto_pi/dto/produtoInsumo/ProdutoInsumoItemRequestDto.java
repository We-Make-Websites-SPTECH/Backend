package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProdutoInsumoItemRequestDto {
    @NotNull
    private Integer insumoId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantidade;

    public ProdutoInsumoItemRequestDto() {
    }

    public ProdutoInsumoItemRequestDto(Integer insumoId, BigDecimal quantidade) {
        this.insumoId = insumoId;
        this.quantidade = quantidade;
    }

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}
