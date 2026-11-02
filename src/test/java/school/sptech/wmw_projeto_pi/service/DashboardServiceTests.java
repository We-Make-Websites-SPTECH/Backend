package school.sptech.wmw_projeto_pi.service;

import org.junit.jupiter.api.Test;
import school.sptech.wmw_projeto_pi.dto.dashboard.DashboardResponseDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.InsumosMaisConsumidosDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoSemanalDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoPeriodoDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.PerdasQuebrasDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.ResumoMovimentacaoDto;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTests {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final MovimentacaoEstoqueRepository movimentacaoRepository =
            mock(MovimentacaoEstoqueRepository.class);
    private final InsumoRepository insumoRepository = mock(InsumoRepository.class);
    private final DashboardService dashboardService =
            new DashboardService(movimentacaoRepository, insumoRepository);

    @Test
    void deveCalcularOsQuatroIndicadores() {
        when(insumoRepository.countByAtivoTrue()).thenReturn(3L);
        when(insumoRepository.countByAtivoFalse()).thenReturn(1L);
        when(movimentacaoRepository.buscarNoPeriodo(any(), any())).thenReturn(List.of(
                movimentacao(1, "Mussarela", UnidadeMedidaEnum.GRAMA, true,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.VENDA, "5000.000"),
                movimentacao(1, "Mussarela", UnidadeMedidaEnum.GRAMA, true,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.VENDA, "3000.000"),
                movimentacao(1, "Mussarela", UnidadeMedidaEnum.GRAMA, true,
                        TipoMovimentacaoEnum.ENTRADA, MotivoMovimentacaoEnum.CANCELAMENTO_VENDA, "1000.000"),
                movimentacao(1, "Mussarela", UnidadeMedidaEnum.GRAMA, true,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.QUEBRA, "500.000"),
                movimentacao(2, "Molho inativo", UnidadeMedidaEnum.GRAMA, false,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.VENDA, "9999.000"),
                movimentacao(3, "Embalagem", UnidadeMedidaEnum.UNIDADE, true,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.VENDA, "40.000"),
                movimentacao(3, "Embalagem", UnidadeMedidaEnum.UNIDADE, true,
                        TipoMovimentacaoEnum.ENTRADA, MotivoMovimentacaoEnum.COMPRA, "100.000"),
                movimentacao(1, "Mussarela", UnidadeMedidaEnum.GRAMA, true,
                        TipoMovimentacaoEnum.SAIDA, MotivoMovimentacaoEnum.AJUSTE, "200.000")
        ));

        DashboardResponseDto resposta = dashboardService.getIndicadores();

        assertThat(resposta.dataFim()).isEqualTo(LocalDate.now(FUSO));
        assertThat(resposta.dataInicio()).isEqualTo(resposta.dataFim().minusDays(29));

        assertThat(resposta.insumosMaisConsumidos())
                .extracting(InsumosMaisConsumidosDto::nome, InsumosMaisConsumidosDto::quantidadeConsumida)
                .containsExactly(
                        tuple("Mussarela", new BigDecimal("7000.000")),
                        tuple("Embalagem", new BigDecimal("40.000"))
                );

        assertThat(resposta.entradasSaidas().totalEntradas())
                .extracting(ResumoMovimentacaoDto::unidadeMedida, ResumoMovimentacaoDto::quantidade)
                .containsExactly(
                        tuple(UnidadeMedidaEnum.GRAMA, new BigDecimal("1000.000")),
                        tuple(UnidadeMedidaEnum.UNIDADE, new BigDecimal("100.000"))
                );

        // O KPI de entradas x saidas considera o historico, inclusive de insumos inativos.
        assertThat(resposta.entradasSaidas().totalSaidas())
                .extracting(ResumoMovimentacaoDto::unidadeMedida, ResumoMovimentacaoDto::quantidade)
                .containsExactly(
                        tuple(UnidadeMedidaEnum.GRAMA, new BigDecimal("18499.000")),
                        tuple(UnidadeMedidaEnum.UNIDADE, new BigDecimal("40.000"))
                );

        assertThat(resposta.entradasSaidas().movimentacoes())
                .extracting(MovimentacaoSemanalDto::tipo, MovimentacaoSemanalDto::unidadeMedida,
                        MovimentacaoSemanalDto::quantidade)
                .containsExactly(
                        tuple(TipoMovimentacaoEnum.ENTRADA, UnidadeMedidaEnum.GRAMA, new BigDecimal("1000.000")),
                        tuple(TipoMovimentacaoEnum.ENTRADA, UnidadeMedidaEnum.UNIDADE, new BigDecimal("100.000")),
                        tuple(TipoMovimentacaoEnum.SAIDA, UnidadeMedidaEnum.GRAMA, new BigDecimal("18499.000")),
                        tuple(TipoMovimentacaoEnum.SAIDA, UnidadeMedidaEnum.UNIDADE, new BigDecimal("40.000"))
                );

        assertThat(resposta.perdasQuebras())
                .extracting(PerdasQuebrasDto::unidadeMedida, PerdasQuebrasDto::quantidade)
                .containsExactly(tuple(UnidadeMedidaEnum.GRAMA, new BigDecimal("500.000")));

        assertThat(resposta.insumosStatus().ativos()).isEqualTo(3L);
        assertThat(resposta.insumosStatus().inativos()).isEqualTo(1L);
        assertThat(resposta.insumosStatus().total()).isEqualTo(4L);
        assertThat(resposta.insumosStatus().percentualAtivos()).isEqualTo(75.0);
        assertThat(resposta.insumosStatus().percentualInativos()).isEqualTo(25.0);
    }

    @Test
    void deveRetornarPercentuaisZeroQuandoNaoHaInsumos() {
        when(insumoRepository.countByAtivoTrue()).thenReturn(0L);
        when(insumoRepository.countByAtivoFalse()).thenReturn(0L);
        when(movimentacaoRepository.buscarNoPeriodo(any(), any())).thenReturn(List.of());

        DashboardResponseDto resposta = dashboardService.getIndicadores();

        assertThat(resposta.insumosMaisConsumidos()).isEmpty();
        assertThat(resposta.perdasQuebras()).isEmpty();
        assertThat(resposta.entradasSaidas().movimentacoes()).isEmpty();
        assertThat(resposta.insumosStatus().total()).isZero();
        assertThat(resposta.insumosStatus().percentualAtivos()).isZero();
        assertThat(resposta.insumosStatus().percentualInativos()).isZero();
    }

    private MovimentacaoPeriodoDto movimentacao(Integer insumoId, String nome, UnidadeMedidaEnum unidade,
                                                Boolean ativo, TipoMovimentacaoEnum tipo,
                                                MotivoMovimentacaoEnum motivo, String quantidade) {
        return new MovimentacaoPeriodoDto(
                insumoId, nome, unidade, ativo, tipo, motivo,
                new BigDecimal(quantidade), LocalDateTime.now(FUSO)
        );
    }
}
