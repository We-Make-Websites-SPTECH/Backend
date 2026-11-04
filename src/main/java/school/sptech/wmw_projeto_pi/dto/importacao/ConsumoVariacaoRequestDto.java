package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class ConsumoVariacaoRequestDto {
    @NotNull
    @DecimalMin("1")
    @DecimalMax("2147483647")
    @Digits(integer = 10, fraction = 0)
    private BigDecimal unidades;

    @NotNull
    @Size(max = 1000)
    private List<@NotNull @Valid ConsumoIngredienteRequestDto> ingredientes;

    public ConsumoVariacaoRequestDto() {
    }

    public ConsumoVariacaoRequestDto(
            BigDecimal unidades,
            List<ConsumoIngredienteRequestDto> ingredientes) {
        this.unidades = unidades;
        this.ingredientes = ingredientes;
    }

    public BigDecimal getUnidades() {
        return unidades;
    }

    public void setUnidades(BigDecimal unidades) {
        this.unidades = unidades;
    }

    public List<ConsumoIngredienteRequestDto> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<ConsumoIngredienteRequestDto> ingredientes) {
        this.ingredientes = ingredientes;
    }
}
