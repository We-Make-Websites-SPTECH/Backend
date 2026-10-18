package school.sptech.wmw_projeto_pi.dto;

import school.sptech.wmw_projeto_pi.entity.Usuario;

public class UsuarioResponseDTO {
    private Integer Id;
    private String nome;
    private String email;
    private Boolean ativo;

    public UsuarioResponseDTO(Integer id, String nome, String email, Boolean ativo) {
        Id = id;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
