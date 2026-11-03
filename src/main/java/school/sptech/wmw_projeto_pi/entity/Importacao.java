package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;

@Entity
@Table(name="importacao")
public class Importacao {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nomeArquivo;
    @Column(columnDefinition="MEDIUMTEXT")
    private String conteudoOriginal;
    @Column(nullable=false)
    private LocalDate dataReferencia;
    @Column(nullable=false)
    private LocalDateTime criadoEm;
    @ManyToOne @JoinColumn(name="usuario_id",nullable=false)
    private Usuario usuario;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private OrigemRegistroEnum origem = OrigemRegistroEnum.TXT;
    public OrigemRegistroEnum getOrigem() { return origem; }
    public void setOrigem(OrigemRegistroEnum origem) { this.origem = origem; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getConteudoOriginal() { return conteudoOriginal; }
    public void setConteudoOriginal(String conteudoOriginal) { this.conteudoOriginal = conteudoOriginal; }
    public LocalDate getDataReferencia() { return dataReferencia; }
    public void setDataReferencia(LocalDate dataReferencia) { this.dataReferencia = dataReferencia; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
