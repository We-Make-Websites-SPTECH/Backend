package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(
        name = "importacao_linha_consumo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"linha_id", "insumo_id"})
)
public class ImportacaoLinhaConsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "linha_id", nullable = false)
    private ImportacaoLinha linha;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 9, fraction = 3)
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadePrevista;

    @PositiveOrZero
    @Digits(integer = 9, fraction = 3)
    @Column(precision = 12, scale = 3)
    private BigDecimal quantidadeEfetivada;

    @Size(max = 255)
    private String aviso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImportacaoLinha getLinha() {
        return linha;
    }

    public void setLinha(ImportacaoLinha linha) {
        this.linha = linha;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getQuantidadePrevista() {
        return quantidadePrevista;
    }

    public void setQuantidadePrevista(BigDecimal quantidadePrevista) {
        this.quantidadePrevista = quantidadePrevista;
    }

    public BigDecimal getQuantidadeEfetivada() {
        return quantidadeEfetivada;
    }

    public void setQuantidadeEfetivada(BigDecimal quantidadeEfetivada) {
        this.quantidadeEfetivada = quantidadeEfetivada;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }
}
