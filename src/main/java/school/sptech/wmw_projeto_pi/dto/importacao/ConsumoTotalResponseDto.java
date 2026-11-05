package school.sptech.wmw_projeto_pi.dto.importacao;

import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;

public class ConsumoTotalResponseDto {
    private Integer insumoId;

    private String nome;

    private UnidadeMedidaEnum unidadeMedida;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UnidadeMedidaEnum getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedidaEnum unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidadePrevista() {
        return quantidadePrevista;
    }

    public void setQuantidadePrevista(BigDecimal quantidadePrevista) {
        this.quantidadePrevista = quantidadePrevista;
    }
}
