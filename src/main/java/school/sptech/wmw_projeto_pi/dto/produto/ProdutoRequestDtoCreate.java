package school.sptech.wmw_projeto_pi.dto.produto;

import school.sptech.wmw_projeto_pi.enums.TipoProdutoEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProdutoRequestDtoCreate {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotNull
    private TipoProdutoEnum tipo;

    @NotNull
    @Positive
    private Integer tamanho;

    public ProdutoRequestDtoCreate() {
    }

    public ProdutoRequestDtoCreate(String nome, TipoProdutoEnum tipo, Integer tamanho) {
        this.nome = nome;
        this.tipo = tipo;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
