package school.sptech.wmw_projeto_pi.dto.importacao;

public class ImportacaoResultadoResponseDto {
    private Long linhaId;

    private boolean jaProcessada;

    private String erroCodigo;

    private String erroMensagem;

    private ImportacaoLinhaResponseDto linha;

    public ImportacaoResultadoResponseDto() {
    }

    public ImportacaoResultadoResponseDto(
            Long linhaId,
            boolean jaProcessada,
            String erroCodigo,
            String erroMensagem,
            ImportacaoLinhaResponseDto linha) {
        this.linhaId = linhaId;
        this.jaProcessada = jaProcessada;
        this.erroCodigo = erroCodigo;
        this.erroMensagem = erroMensagem;
        this.linha = linha;
    }

    public Long getLinhaId() {
        return linhaId;
    }

    public void setLinhaId(Long linhaId) {
        this.linhaId = linhaId;
    }

    public boolean isJaProcessada() {
        return jaProcessada;
    }

    public void setJaProcessada(boolean jaProcessada) {
        this.jaProcessada = jaProcessada;
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

    public ImportacaoLinhaResponseDto getLinha() {
        return linha;
    }

    public void setLinha(ImportacaoLinhaResponseDto linha) {
        this.linha = linha;
    }
}
