package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    //private Cargo cargo;
    private String nome;
    private String senha;
    private Boolean ativo;
    // private Usuario createdBy;
    // private LocalDate createdAt;
    // private Usuario updatedBy;
    // private Localdate updatedAt;


    public Usuario() {
    }

    public Usuario(Integer id, String nome, String senha, Boolean ativo) {
        Id = id;
        this.nome = nome;
        this.senha = senha;
        this.ativo = ativo;
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
