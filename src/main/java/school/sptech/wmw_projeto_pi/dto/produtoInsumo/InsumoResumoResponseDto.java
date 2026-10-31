package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

public class InsumoResumoResponseDto {
    private Integer id;
    private String nome;
    private UnidadeMedidaEnum unidadeMedida;

    public InsumoResumoResponseDto() {
    }

    public InsumoResumoResponseDto(Integer id, String nome, UnidadeMedidaEnum unidadeMedida) {
        this.id = id;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
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

    public UnidadeMedidaEnum getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedidaEnum unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
