package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ConsumoIngredienteRequestDto {
    @NotNull
    @Positive
    private Integer insumoId;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 9, fraction = 3)
    private BigDecimal quantidade;

    public ConsumoIngredienteRequestDto() {
    }

    public ConsumoIngredienteRequestDto(
            Integer insumoId,
            BigDecimal quantidade) {
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
