package school.sptech.wmw_projeto_pi.dto.estoque;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AjusteEstoqueRequestDto {
    @NotNull
    @Positive
    private Integer insumoId;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal novoSaldo;

    @Size(max = 255)
    private String observacao;

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public BigDecimal getNovoSaldo() {
        return novoSaldo;
    }

    public void setNovoSaldo(BigDecimal novoSaldo) {
        this.novoSaldo = novoSaldo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
