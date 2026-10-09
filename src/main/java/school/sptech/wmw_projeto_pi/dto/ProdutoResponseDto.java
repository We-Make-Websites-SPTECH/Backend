package school.sptech.wmw_projeto_pi.dto;

import jakarta.persistence.ManyToOne;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

import java.time.LocalDate;

public class ProdutoResponseDto {
    private Integer id;
    private String nome;
    private Boolean ativo;
    private TamanhoEnum tamanho;
    private LocalDate createdAt;
    private Usuario createdBy;
    private LocalDate updatedAt;
    private Usuario updatedBy;

    public ProdutoResponseDto() {
    }

    public ProdutoResponseDto(Integer id, String nome, Boolean ativo, TamanhoEnum tamanho, LocalDate createdAt, Usuario createdBy, LocalDate updatedAt, Usuario updatedBy) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.tamanho = tamanho;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Usuario getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Usuario updatedBy) {
        this.updatedBy = updatedBy;
    }
}
