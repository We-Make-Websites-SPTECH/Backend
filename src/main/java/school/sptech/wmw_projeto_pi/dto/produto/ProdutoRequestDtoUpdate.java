package school.sptech.wmw_projeto_pi.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

public class ProdutoRequestDtoUpdate {

    @NotBlank
    private String nome;

    @NotNull
    private Boolean ativo;

    @NotNull
    private TamanhoEnum tamanho;

    public ProdutoRequestDtoUpdate() {
    }

    public ProdutoRequestDtoUpdate(String nome, Boolean ativo, TamanhoEnum tamanho) {
        this.nome = nome;
        this.ativo = ativo;
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

    public TamanhoEnum getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoEnum tamanho) {
        this.tamanho = tamanho;
    }
}
