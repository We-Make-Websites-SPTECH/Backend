package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

@Entity
@Table(name="importacao_linha", uniqueConstraints=@UniqueConstraint(columnNames={"importacao_id", "numero_linha"}))
public class ImportacaoLinha {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name="importacao_id",nullable=false)
    private Importacao importacao;
    @Column(nullable=false)
    private Integer numeroLinha;
    @Column(columnDefinition="MEDIUMTEXT")
    private String textoOriginal;
    @Column(columnDefinition="MEDIUMTEXT")
    private String nomeAtual;
    @Column(columnDefinition="MEDIUMTEXT")
    private String idOriginal;
    @Column(columnDefinition="MEDIUMTEXT")
    private String quantidadeOriginal;
    @ManyToOne @JoinColumn(name="produto_id")
    private Produto produto;

    private Integer quantidadeAtual;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private StatusImportacaoLinhaEnum status;
    @Column(length=60)
    private String erroCodigo;
    @Column(length=255)
    private String erroMensagem;

    private LocalDateTime processadoEm;
    private java.time.LocalDateTime atualizadoEm;
    public void marcarCorrecao() { atualizadoEm = java.time.LocalDateTime.now(); }
    @Version
    private Long versao;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Importacao getImportacao() { return importacao; }
    public void setImportacao(Importacao importacao) { this.importacao = importacao; }
    public Integer getNumeroLinha() { return numeroLinha; }
    public void setNumeroLinha(Integer numeroLinha) { this.numeroLinha = numeroLinha; }
    public String getTextoOriginal() { return textoOriginal; }
    public void setTextoOriginal(String textoOriginal) { this.textoOriginal = textoOriginal; }
    public String getNomeAtual() { return nomeAtual; }
    public void setNomeAtual(String nomeAtual) { this.nomeAtual = nomeAtual; }
    public String getIdOriginal() { return idOriginal; }
    public void setIdOriginal(String idOriginal) { this.idOriginal = idOriginal; }
    public String getQuantidadeOriginal() { return quantidadeOriginal; }
    public void setQuantidadeOriginal(String quantidadeOriginal) { this.quantidadeOriginal = quantidadeOriginal; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
    public StatusImportacaoLinhaEnum getStatus() { return status; }
    public void setStatus(StatusImportacaoLinhaEnum status) { this.status = status; }
    public String getErroCodigo() { return erroCodigo; }
    public void setErroCodigo(String erroCodigo) { this.erroCodigo = erroCodigo; }
    public String getErroMensagem() { return erroMensagem; }
    public void setErroMensagem(String erroMensagem) { this.erroMensagem = erroMensagem; }
    public LocalDateTime getProcessadoEm() { return processadoEm; }
    public void setProcessadoEm(LocalDateTime processadoEm) { this.processadoEm = processadoEm; }
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
