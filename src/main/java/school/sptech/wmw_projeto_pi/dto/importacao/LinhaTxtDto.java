package school.sptech.wmw_projeto_pi.dto.importacao;

public class LinhaTxtDto {
    private Integer numero;

    private String id;

    private String nome;

    private String quantidade;

    private String erro;

    public LinhaTxtDto() {
    }

    public LinhaTxtDto(Integer numero, String id, String nome, String quantidade, String erro) {
        this.numero = numero;
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.erro = erro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
