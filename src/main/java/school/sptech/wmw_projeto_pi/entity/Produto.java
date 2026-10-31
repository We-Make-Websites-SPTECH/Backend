package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import school.sptech.wmw_projeto_pi.enums.TipoProdutoEnum;

import java.time.LocalDateTime;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProdutoEnum tipo;
    @Column(nullable = false)
    private Integer tamanho;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Usuario createdBy;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private Usuario updatedBy;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        this.createdAt = agora;
        this.updatedAt = agora;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Produto() {
    }

    public Produto(Integer id, String nome, Boolean ativo, TipoProdutoEnum tipo, Integer tamanho, LocalDateTime createdAt, Usuario createdBy, LocalDateTime updatedAt, Usuario updatedBy) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.tipo = tipo;
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

    public Integer getTamanho() { return tamanho; }

    public void setTamanho(Integer tamanho) { this.tamanho = tamanho; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Usuario getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Usuario updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    public TipoProdutoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoProdutoEnum tipo) {
        this.tipo = tipo;
    }
}