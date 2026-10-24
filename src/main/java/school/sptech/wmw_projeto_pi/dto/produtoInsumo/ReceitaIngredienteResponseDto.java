package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import java.math.BigDecimal;

public class ReceitaIngredienteResponseDto {

    private Integer id;
    private InsumoResumoResponseDto insumo;
    private BigDecimal quantidade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InsumoResumoResponseDto getInsumo() {
        return insumo;
    }

    public void setInsumo(InsumoResumoResponseDto insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}