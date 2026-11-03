package school.sptech.wmw_projeto_pi.dto.importacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;

public final class LancamentoManualDtos {
    private LancamentoManualDtos() {}
    public record Item(@NotNull @Positive Integer produtoId, @NotNull @Positive Integer quantidade) {}
    public record Pedido(@NotNull LocalDate dataReferencia,
                         @NotEmpty @Size(max=100) List<@NotNull @Valid Item> itens) {}
    public record CorrecaoManual(@NotNull @Positive Integer produtoId, @NotNull @Positive Integer quantidade,
                                 @NotNull @PositiveOrZero Long versao) {}
    public record TotalConsumo(Integer insumoId,BigDecimal quantidadePrevista) {}
    public record Previa(Resumo registro,List<Linha> linhas,List<TotalConsumo> consumoAgregado) {}
}
