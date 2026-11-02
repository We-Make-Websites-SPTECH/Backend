package school.sptech.wmw_projeto_pi.dto.insumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoResponseDto {

    private Long id;

    private String tipo;

    private String motivo;

    private BigDecimal quantidade;

    private LocalDateTime dataHora;

    public MovimentacaoResponseDto() {
    }

    public MovimentacaoResponseDto(Long id, String tipo, String motivo, BigDecimal quantidade, LocalDateTime dataHora) {
        this.id = id;
        this.tipo = tipo;
        this.motivo = motivo;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
