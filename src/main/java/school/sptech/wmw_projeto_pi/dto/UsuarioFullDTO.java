package school.sptech.wmw_projeto_pi.dto;

public class UsuarioFullDTO {
    private Integer Id;
    private String nome;
    private String email;
    private String senha;
    private Boolean ativo;

    public UsuarioFullDTO(Integer id, String nome, String email, String senha, Boolean ativo) {
        Id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
    }

    public UsuarioFullDTO() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
