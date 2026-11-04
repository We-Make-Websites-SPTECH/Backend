package school.sptech.wmw_projeto_pi.dto.usuario;

import school.sptech.wmw_projeto_pi.entity.Usuario;

public class UsuarioResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private Boolean ativo;

    public UsuarioResponseDTO(Integer id, String nome, String email, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
    }

    public UsuarioResponseDTO() {
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo()
        );
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
