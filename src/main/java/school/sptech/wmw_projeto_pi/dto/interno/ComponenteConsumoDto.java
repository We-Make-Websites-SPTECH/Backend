package school.sptech.wmw_projeto_pi.dto.interno;

import school.sptech.wmw_projeto_pi.entity.Insumo;

import java.math.BigDecimal;

public class ComponenteConsumoDto {
    private Insumo insumo;

    private BigDecimal quantidade;

    public ComponenteConsumoDto() {
    }

    public ComponenteConsumoDto(Insumo insumo, BigDecimal quantidade) {
        this.insumo = insumo;
        this.quantidade = quantidade;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}
