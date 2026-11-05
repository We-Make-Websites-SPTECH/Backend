package school.sptech.wmw_projeto_pi.dto.importacao;

import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.math.BigDecimal;

public class ConsumoInsumoResponseDto {
    private Long id;

    private Integer insumoId;

    private String nome;

    private UnidadeMedidaEnum unidadeMedida;

    private BigDecimal previsto;

    private BigDecimal efetivado;

    private String aviso;

    public ConsumoInsumoResponseDto() {
    }

    public ConsumoInsumoResponseDto(
            Long id,
            Integer insumoId,
            BigDecimal previsto,
            BigDecimal efetivado,
            String aviso) {
        this.id = id;
        this.insumoId = insumoId;
        this.previsto = previsto;
        this.efetivado = efetivado;
        this.aviso = aviso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPrevisto() {
        return previsto;
    }

    public void setPrevisto(BigDecimal previsto) {
        this.previsto = previsto;
    }

    public BigDecimal getEfetivado() {
        return efetivado;
    }

    public void setEfetivado(BigDecimal efetivado) {
        this.efetivado = efetivado;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }
}
