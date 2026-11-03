package school.sptech.wmw_projeto_pi.dto.importacao;

import java.math.BigDecimal;

public class ConsumoTotalResponseDto {
    private Integer insumoId;

    private BigDecimal quantidadePrevista;

    public ConsumoTotalResponseDto() {
    }

    public ConsumoTotalResponseDto(
            Integer insumoId,
            BigDecimal quantidadePrevista) {
        this.insumoId = insumoId;
        this.quantidadePrevista = quantidadePrevista;
    }

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public BigDecimal getQuantidadePrevista() {
        return quantidadePrevista;
    }

    public void setQuantidadePrevista(BigDecimal quantidadePrevista) {
        this.quantidadePrevista = quantidadePrevista;
    }
}
