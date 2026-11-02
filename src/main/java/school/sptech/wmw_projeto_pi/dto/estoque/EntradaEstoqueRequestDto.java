package school.sptech.wmw_projeto_pi.dto.estoque;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;

import java.math.BigDecimal;

public class EntradaEstoqueRequestDto {
    @NotNull
    private Integer insumoId;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal quantidadeBruta;

    @NotNull
    private MotivoMovimentacaoEnum motivo;

    @Size(max = 255)
    private String observacao;

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public BigDecimal getQuantidadeBruta() {
        return quantidadeBruta;
    }

    public void setQuantidadeBruta(BigDecimal quantidadeBruta) {
        this.quantidadeBruta = quantidadeBruta;
    }

    public MotivoMovimentacaoEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoMovimentacaoEnum motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
