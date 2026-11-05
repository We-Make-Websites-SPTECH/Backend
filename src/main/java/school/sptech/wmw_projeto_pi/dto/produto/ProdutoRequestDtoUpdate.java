package school.sptech.wmw_projeto_pi.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import school.sptech.wmw_projeto_pi.enums.TipoProdutoEnum;

public class ProdutoRequestDtoUpdate {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotNull
    private Boolean ativo;

    @NotNull
    private TipoProdutoEnum tipo;

    @NotNull
    @Positive
    private Integer tamanho;

    public ProdutoRequestDtoUpdate() {
    }

    public ProdutoRequestDtoUpdate(String nome, Boolean ativo, TipoProdutoEnum tipo, Integer tamanho) {
        this.nome = nome;
        this.ativo = ativo;
        this.tipo = tipo;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public TipoProdutoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoProdutoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }
}
