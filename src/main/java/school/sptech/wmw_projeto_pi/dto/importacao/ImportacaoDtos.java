package school.sptech.wmw_projeto_pi.dto.importacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

public final class ImportacaoDtos {
    private ImportacaoDtos() {}
    public record Correcao(@NotNull @Positive Integer produtoId, @NotBlank String nome,
                          @NotNull @Positive Integer quantidade, @NotNull @PositiveOrZero Long versao) {}
    public record Selecao(@NotNull @Positive Long linhaId, @NotNull @PositiveOrZero Long versao) {}
    public record Confirmacao(@NotEmpty @Size(max=10000) List<@NotNull @Valid Selecao> linhas) {}
    public record Consumo(Long id,Integer insumoId,BigDecimal previsto,BigDecimal efetivado,String aviso) {}
    public record Linha(Long id,Integer numeroLinha,String original,String idOriginal,String quantidadeOriginal,
                        Integer produtoId,String nome,Integer quantidade,StatusImportacaoLinhaEnum status,
                        Long versao,String erroCodigo,String erroMensagem,LocalDateTime processadoEm,List<Consumo> consumo) {}
    public record Resumo(Long id,school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum origem,String nomeArquivo,LocalDate dataReferencia,LocalDateTime criadoEm,
                         long pendentes,long invalidas,long processadas) {}
    public record Resultado(Long linhaId,boolean jaProcessada,String erroCodigo,String erroMensagem,Linha linha) {}
}
