package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

public class ProdutoResumoResponseDto {
    private Integer id;
    private String nome;
    private TamanhoEnum tamanho;

    public ProdutoResumoResponseDto() {
    }

    public ProdutoResumoResponseDto(Integer id, String nome, TamanhoEnum tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
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

    public TamanhoEnum getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoEnum tamanho) {
        this.tamanho = tamanho;
    }
}
