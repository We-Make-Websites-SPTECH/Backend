package school.sptech.wmw_projeto_pi.dto.importacao;

import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ImportacaoResumoResponseDto {
    private Long id;

    private OrigemRegistroEnum origem;

    private String nomeArquivo;

    private LocalDate dataReferencia;

    private LocalDateTime createdAt;

    private StatusImportacaoEnum status;

    private long pendentes;

    private long invalidas;

    private long processadas;

    public ImportacaoResumoResponseDto() {
    }

    public ImportacaoResumoResponseDto(
            Long id,
            OrigemRegistroEnum origem,
            String nomeArquivo,
            LocalDate dataReferencia,
            LocalDateTime createdAt,
            StatusImportacaoEnum status,
            long pendentes,
            long invalidas,
            long processadas) {
        this.id = id;
        this.origem = origem;
        this.nomeArquivo = nomeArquivo;
        this.dataReferencia = dataReferencia;
        this.createdAt = createdAt;
        this.status = status;
        this.pendentes = pendentes;
        this.invalidas = invalidas;
        this.processadas = processadas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrigemRegistroEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRegistroEnum origem) {
        this.origem = origem;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public StatusImportacaoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusImportacaoEnum status) {
        this.status = status;
    }

    public long getPendentes() {
        return pendentes;
    }

    public void setPendentes(long pendentes) {
        this.pendentes = pendentes;
    }

    public long getInvalidas() {
        return invalidas;
    }

    public void setInvalidas(long invalidas) {
        this.invalidas = invalidas;
    }

    public long getProcessadas() {
        return processadas;
    }

    public void setProcessadas(long processadas) {
        this.processadas = processadas;
    }
}
