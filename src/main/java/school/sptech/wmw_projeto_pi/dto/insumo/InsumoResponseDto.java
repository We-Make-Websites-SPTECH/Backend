package school.sptech.wmw_projeto_pi.dto.insumo;

import java.math.BigDecimal;

public class InsumoResponseDto {

    private Integer id;

    private String nome;

    private String unidadeMedida;

    private BigDecimal saldo;

    private Boolean ativo;

    public InsumoResponseDto() {
    }

    public InsumoResponseDto(Integer id, String nome, String unidadeMedida, BigDecimal saldo, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.saldo = saldo;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
