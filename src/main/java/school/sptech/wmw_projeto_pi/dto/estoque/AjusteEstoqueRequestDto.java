package school.sptech.wmw_projeto_pi.dto.estoque;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class AjusteEstoqueRequestDto {
    @NotNull
    private Integer insumoId;

    @NotNull
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
