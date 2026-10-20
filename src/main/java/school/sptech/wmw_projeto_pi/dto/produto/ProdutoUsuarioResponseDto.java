package school.sptech.wmw_projeto_pi.dto.produto;

public class ProdutoUsuarioResponseDto {
    private Integer Id;
    private String nome;

    public ProdutoUsuarioResponseDto() {
    }

    public ProdutoUsuarioResponseDto(Integer id, String nome) {
        Id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
