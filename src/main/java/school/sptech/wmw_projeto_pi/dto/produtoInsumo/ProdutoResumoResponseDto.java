package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

public class ProdutoResumoResponseDto {
    private Integer id;
    private String nome;
    private Integer tamanho;

    public ProdutoResumoResponseDto() {
    }

    public ProdutoResumoResponseDto(Integer id, String nome, Integer tamanho) {
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

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }
}
