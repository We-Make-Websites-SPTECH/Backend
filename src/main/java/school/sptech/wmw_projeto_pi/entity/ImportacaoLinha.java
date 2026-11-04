package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "importacao_linha",
        uniqueConstraints = @UniqueConstraint(columnNames = {"importacao_id", "numero_linha"})
)
public class ImportacaoLinha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "importacao_id", nullable = false)
    private Importacao importacao;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer numeroLinha;

    @Size(max = 255)
    private String nomeAtual;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Integer quantidadeAtual;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StatusImportacaoLinhaEnum status;

    @Size(max = 60)
    @Column(length = 60)
    private String erroCodigo;

    @Size(max = 255)
    private String erroMensagem;

    private LocalDateTime processadoEm;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long valor) {
        this.id = valor;
    }

    public Importacao getImportacao() {
        return importacao;
    }

    public void setImportacao(Importacao valor) {
        this.importacao = valor;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer valor) {
        this.numeroLinha = valor;
    }

    public String getNomeAtual() {
        return nomeAtual;
    }

    public void setNomeAtual(String valor) {
        this.nomeAtual = valor;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto valor) {
        this.produto = valor;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer valor) {
        this.quantidadeAtual = valor;
    }

    public StatusImportacaoLinhaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusImportacaoLinhaEnum valor) {
        this.status = valor;
    }

    public String getErroCodigo() {
        return erroCodigo;
    }

    public void setErroCodigo(String valor) {
        this.erroCodigo = valor;
    }

    public String getErroMensagem() {
        return erroMensagem;
    }

    public void setErroMensagem(String valor) {
        this.erroMensagem = valor;
    }

    public LocalDateTime getProcessadoEm() {
        return processadoEm;
    }

    public void setProcessadoEm(LocalDateTime valor) {
        this.processadoEm = valor;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime valor) {
        this.updatedAt = valor;
    }

    public void atualizarPrevia() {
        updatedAt = LocalDateTime.now();
    }
}
