package school.sptech.wmw_projeto_pi.dto.importacao;

import java.util.List;

public class LancamentoManualPreviaResponseDto {
    private ImportacaoResumoResponseDto registro;

    private List<ImportacaoLinhaResponseDto> linhas;

    private List<ConsumoTotalResponseDto> consumoAgregado;

    public LancamentoManualPreviaResponseDto() {
    }

    public LancamentoManualPreviaResponseDto(
            ImportacaoResumoResponseDto registro,
            List<ImportacaoLinhaResponseDto> linhas,
            List<ConsumoTotalResponseDto> consumoAgregado) {
        this.registro = registro;
        this.linhas = linhas;
        this.consumoAgregado = consumoAgregado;
    }

    public ImportacaoResumoResponseDto getRegistro() {
        return registro;
    }

    public void setRegistro(ImportacaoResumoResponseDto registro) {
        this.registro = registro;
    }

    public List<ImportacaoLinhaResponseDto> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<ImportacaoLinhaResponseDto> linhas) {
        this.linhas = linhas;
    }

    public List<ConsumoTotalResponseDto> getConsumoAgregado() {
        return consumoAgregado;
    }

    public void setConsumoAgregado(List<ConsumoTotalResponseDto> consumoAgregado) {
        this.consumoAgregado = consumoAgregado;
    }
}
