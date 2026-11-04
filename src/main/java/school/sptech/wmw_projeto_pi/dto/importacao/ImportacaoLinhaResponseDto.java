package school.sptech.wmw_projeto_pi.dto.importacao;

import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

import java.time.LocalDateTime;
import java.util.List;

public class ImportacaoLinhaResponseDto {
    private Long id;

    private Integer numeroLinha;

    private Integer produtoId;

    private String nome;

    private Integer quantidade;

    private StatusImportacaoLinhaEnum status;

    private String erroCodigo;

    private String erroMensagem;

    private LocalDateTime processadoEm;

    private List<ConsumoInsumoResponseDto> consumo;

    public ImportacaoLinhaResponseDto() {
    }

    public ImportacaoLinhaResponseDto(
            Long id,
            Integer numeroLinha,
            Integer produtoId,
            String nome,
            Integer quantidade,
            StatusImportacaoLinhaEnum status,
            String erroCodigo,
            String erroMensagem,
            LocalDateTime processadoEm,
            List<ConsumoInsumoResponseDto> consumo) {
        this.id = id;
        this.numeroLinha = numeroLinha;
        this.produtoId = produtoId;
        this.nome = nome;
        this.quantidade = quantidade;
        this.status = status;
        this.erroCodigo = erroCodigo;
        this.erroMensagem = erroMensagem;
        this.processadoEm = processadoEm;
        this.consumo = consumo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public StatusImportacaoLinhaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusImportacaoLinhaEnum status) {
        this.status = status;
    }

    public String getErroCodigo() {
        return erroCodigo;
    }

    public void setErroCodigo(String erroCodigo) {
        this.erroCodigo = erroCodigo;
    }

    public String getErroMensagem() {
        return erroMensagem;
    }

    public void setErroMensagem(String erroMensagem) {
        this.erroMensagem = erroMensagem;
    }

    public LocalDateTime getProcessadoEm() {
        return processadoEm;
    }

    public void setProcessadoEm(LocalDateTime processadoEm) {
        this.processadoEm = processadoEm;
    }

    public List<ConsumoInsumoResponseDto> getConsumo() {
        return consumo;
    }

    public void setConsumo(List<ConsumoInsumoResponseDto> consumo) {
        this.consumo = consumo;
    }
}
