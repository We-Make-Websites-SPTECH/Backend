package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "importacao")
public class Importacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    private String nomeArquivo;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataReferencia;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OrigemRegistroEnum origem = OrigemRegistroEnum.TXT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private StatusImportacaoEnum status = StatusImportacaoEnum.PREVIA;

    public Long getId() {
        return id;
    }

    public void setId(Long valor) {
        this.id = valor;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String valor) {
        this.nomeArquivo = valor;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate valor) {
        this.dataReferencia = valor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime valor) {
        this.createdAt = valor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario valor) {
        this.usuario = valor;
    }

    public OrigemRegistroEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRegistroEnum valor) {
        this.origem = valor;
    }

    public StatusImportacaoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusImportacaoEnum valor) {
        this.status = valor;
    }
}
