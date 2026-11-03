package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import java.time.*;
import java.math.BigDecimal;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

@Entity
@Table(name="importacao_linha_consumo", uniqueConstraints=@UniqueConstraint(columnNames={"linha_id", "insumo_id"}))
public class ImportacaoLinhaConsumo {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name="linha_id",nullable=false)
    private ImportacaoLinha linha;
    @ManyToOne @JoinColumn(name="insumo_id",nullable=false)
    private Insumo insumo;
    @Column(nullable=false,precision=12,scale=3)
    private BigDecimal quantidadePrevista;
    @Column(precision=12,scale=3)
    private BigDecimal quantidadeEfetivada;
    @Column(length=255)
    private String aviso;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ImportacaoLinha getLinha() { return linha; }
    public void setLinha(ImportacaoLinha linha) { this.linha = linha; }
    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }
    public BigDecimal getQuantidadePrevista() { return quantidadePrevista; }
    public void setQuantidadePrevista(BigDecimal quantidadePrevista) { this.quantidadePrevista = quantidadePrevista; }
    public BigDecimal getQuantidadeEfetivada() { return quantidadeEfetivada; }
    public void setQuantidadeEfetivada(BigDecimal quantidadeEfetivada) { this.quantidadeEfetivada = quantidadeEfetivada; }
    public String getAviso() { return aviso; }
    public void setAviso(String aviso) { this.aviso = aviso; }
}
