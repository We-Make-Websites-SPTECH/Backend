package school.sptech.wmw_projeto_pi.entity;


import jakarta.persistence.*;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;

import java.time.LocalDateTime;

@Entity
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedidaEnum unidade;
    private Double quantidadeAtual;
    private Boolean ativo;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Usuario createdBy;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "updated_by_id")
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

    public Insumo() {
    }

    public Insumo(Integer id, String nome, UnidadeMedidaEnum unidade, Double quantidadeAtual, Boolean ativo, LocalDateTime createdAt, Usuario createdBy, LocalDateTime updatedAt, Usuario updatedBy) {
        this.id = id;
        this.nome = nome;
        this.unidade = unidade;
        this.quantidadeAtual = quantidadeAtual;
        this.ativo = ativo;
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

    public UnidadeMedidaEnum getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeMedidaEnum unidade) {
        this.unidade = unidade;
    }

    public Double getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Double quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

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


}
