package school.sptech.wmw_projeto_pi.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

public class ProdutoRequestDtoCreate {

    @NotBlank
    private String nome;

    @NotNull
    private TamanhoEnum tamanho;

    public ProdutoRequestDtoCreate() {
    }

    public ProdutoRequestDtoCreate(String nome, TamanhoEnum tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TamanhoEnum getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoEnum tamanho) {
        this.tamanho = tamanho;
    }
}
