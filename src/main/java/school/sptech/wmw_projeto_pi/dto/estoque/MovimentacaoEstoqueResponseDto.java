package school.sptech.wmw_projeto_pi.dto.estoque;

import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoqueResponseDto {
    private Long id;
    private Integer insumoId;
    private Integer usuarioId;
    private TipoMovimentacaoEnum tipo;
    private MotivoMovimentacaoEnum motivo;
    private BigDecimal quantidade;
    private BigDecimal saldoAtual;
    private String observacao;
    private LocalDateTime dataHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoMovimentacaoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacaoEnum tipo) {
        this.tipo = tipo;
    }

    public MotivoMovimentacaoEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoMovimentacaoEnum motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

}
