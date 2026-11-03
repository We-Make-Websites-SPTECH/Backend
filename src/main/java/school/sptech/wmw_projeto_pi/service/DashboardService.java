package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.dto.dashboard.DashboardResponseDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.EntradasSaidasDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.InsumosMaisConsumidosDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.InsumosStatusDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoPeriodoDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoSemanalDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.PerdasQuebrasDto;
import school.sptech.wmw_projeto_pi.dto.dashboard.ResumoMovimentacaoDto;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final int DIAS_PERIODO = 30;
    private static final int LIMITE_RANKING_POR_UNIDADE = 10;
    private static final DateTimeFormatter FORMATO_SEMANA = DateTimeFormatter.ofPattern("dd/MM");
    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final InsumoRepository insumoRepository;

    public DashboardService(MovimentacaoEstoqueRepository movimentacaoRepository,
                            InsumoRepository insumoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.insumoRepository = insumoRepository;
    }

    public DashboardResponseDto getIndicadores() {
        LocalDate dataFim = LocalDate.now(FUSO);
        LocalDate dataInicio = dataFim.minusDays(DIAS_PERIODO - 1L);

        List<MovimentacaoPeriodoDto> movimentacoes = movimentacaoRepository.buscarNoPeriodo(
                dataInicio.atStartOfDay(),
                dataFim.plusDays(1).atStartOfDay()
        );

        return new DashboardResponseDto(
                dataInicio,
                dataFim,
                calcularInsumosMaisConsumidos(movimentacoes),
                calcularEntradasSaidas(movimentacoes),
                calcularPerdasQuebras(movimentacoes),
                calcularStatusInsumos()
        );
    }

    private List<InsumosMaisConsumidosDto> calcularInsumosMaisConsumidos(List<MovimentacaoPeriodoDto> movimentacoes) {
        Map<Integer, ConsumoAcumulado> acumulado = new LinkedHashMap<>();

        for (MovimentacaoPeriodoDto movimentacao : movimentacoes) {
            if (Boolean.FALSE.equals(movimentacao.ativo())) {
                continue;
            }

            BigDecimal delta;
            if (movimentacao.tipo() == TipoMovimentacaoEnum.SAIDA
                    && movimentacao.motivo() == MotivoMovimentacaoEnum.VENDA) {
                delta = movimentacao.quantidade();
            } else if (movimentacao.tipo() == TipoMovimentacaoEnum.ENTRADA
                    && movimentacao.motivo() == MotivoMovimentacaoEnum.CANCELAMENTO_VENDA) {
                delta = movimentacao.quantidade().negate();
            } else {
                continue;
            }

            ConsumoAcumulado atual = acumulado.get(movimentacao.insumoId());
            BigDecimal quantidade = (atual == null ? BigDecimal.ZERO : atual.quantidade()).add(delta);
            acumulado.put(movimentacao.insumoId(), new ConsumoAcumulado(
                    movimentacao.nome(),
                    movimentacao.unidadeMedida(),
                    quantidade
            ));
        }

        List<InsumosMaisConsumidosDto> ordenados = acumulado.entrySet().stream()
                .filter(entrada -> entrada.getValue().quantidade().signum() > 0)
                .map(entrada -> new InsumosMaisConsumidosDto(
                        entrada.getKey(),
                        entrada.getValue().nome(),
                        entrada.getValue().unidadeMedida(),
                        normalizar(entrada.getValue().quantidade())
                ))
                .sorted(Comparator
                        .comparing(InsumosMaisConsumidosDto::unidadeMedida)
                        .thenComparing(InsumosMaisConsumidosDto::quantidadeConsumida, Comparator.reverseOrder()))
                .toList();

        return limitarPorUnidade(ordenados);
    }

    private List<InsumosMaisConsumidosDto> limitarPorUnidade(List<InsumosMaisConsumidosDto> lista) {
        Map<UnidadeMedidaEnum, Integer> contagem = new EnumMap<>(UnidadeMedidaEnum.class);
        List<InsumosMaisConsumidosDto> resultado = new ArrayList<>();

        for (InsumosMaisConsumidosDto item : lista) {
            int quantidadeNaUnidade = contagem.getOrDefault(item.unidadeMedida(), 0);
            if (quantidadeNaUnidade >= LIMITE_RANKING_POR_UNIDADE) {
                continue;
            }
            contagem.put(item.unidadeMedida(), quantidadeNaUnidade + 1);
            resultado.add(item);
        }

        return resultado;
    }

    private EntradasSaidasDto calcularEntradasSaidas(List<MovimentacaoPeriodoDto> movimentacoes) {
        Map<ChaveSemanaTipoUnidade, BigDecimal> serie = new LinkedHashMap<>();
        Map<UnidadeMedidaEnum, BigDecimal> totalEntradas = new EnumMap<>(UnidadeMedidaEnum.class);
        Map<UnidadeMedidaEnum, BigDecimal> totalSaidas = new EnumMap<>(UnidadeMedidaEnum.class);

        for (MovimentacaoPeriodoDto movimentacao : movimentacoes) {
            if (movimentacao.motivo() == MotivoMovimentacaoEnum.AJUSTE) {
                continue;
            }

            LocalDate semana = inicioDaSemana(movimentacao.dataHora().toLocalDate());
            ChaveSemanaTipoUnidade chave = new ChaveSemanaTipoUnidade(
                    semana,
                    movimentacao.tipo(),
                    movimentacao.unidadeMedida()
            );
            serie.merge(chave, movimentacao.quantidade(), BigDecimal::add);

            if (movimentacao.tipo() == TipoMovimentacaoEnum.ENTRADA) {
                totalEntradas.merge(movimentacao.unidadeMedida(), movimentacao.quantidade(), BigDecimal::add);
            } else {
                totalSaidas.merge(movimentacao.unidadeMedida(), movimentacao.quantidade(), BigDecimal::add);
            }
        }

        List<MovimentacaoSemanalDto> movimentacoesSemanais = serie.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<ChaveSemanaTipoUnidade, BigDecimal> entrada) -> entrada.getKey().semana())
                        .thenComparing(entrada -> entrada.getKey().tipo())
                        .thenComparing(entrada -> entrada.getKey().unidadeMedida()))
                .map(entrada -> new MovimentacaoSemanalDto(
                        entrada.getKey().semana().format(FORMATO_SEMANA),
                        entrada.getKey().tipo(),
                        entrada.getKey().unidadeMedida(),
                        normalizar(entrada.getValue())
                ))
                .toList();

        return new EntradasSaidasDto(
                movimentacoesSemanais,
                montarResumos(totalEntradas),
                montarResumos(totalSaidas)
        );
    }

    private List<ResumoMovimentacaoDto> montarResumos(Map<UnidadeMedidaEnum, BigDecimal> totais) {
        return totais.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entrada -> new ResumoMovimentacaoDto(
                        entrada.getKey(),
                        normalizar(entrada.getValue())
                ))
                .toList();
    }

    private List<PerdasQuebrasDto> calcularPerdasQuebras(List<MovimentacaoPeriodoDto> movimentacoes) {
        Map<ChaveSemanaUnidade, BigDecimal> perdas = new LinkedHashMap<>();

        for (MovimentacaoPeriodoDto movimentacao : movimentacoes) {
            if (movimentacao.tipo() != TipoMovimentacaoEnum.SAIDA
                    || movimentacao.motivo() != MotivoMovimentacaoEnum.QUEBRA) {
                continue;
            }

            LocalDate semana = inicioDaSemana(movimentacao.dataHora().toLocalDate());
            ChaveSemanaUnidade chave = new ChaveSemanaUnidade(semana, movimentacao.unidadeMedida());
            perdas.merge(chave, movimentacao.quantidade(), BigDecimal::add);
        }

        return perdas.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<ChaveSemanaUnidade, BigDecimal> entrada) -> entrada.getKey().semana())
                        .thenComparing(entrada -> entrada.getKey().unidadeMedida()))
                .map(entrada -> new PerdasQuebrasDto(
                        entrada.getKey().semana().format(FORMATO_SEMANA),
                        entrada.getKey().unidadeMedida(),
                        normalizar(entrada.getValue())
                ))
                .toList();
    }

    private InsumosStatusDto calcularStatusInsumos() {
        long ativos = valorOuZero(insumoRepository.countByAtivoTrue());
        long inativos = valorOuZero(insumoRepository.countByAtivoFalse());
        long total = ativos + inativos;

        return new InsumosStatusDto(
                ativos,
                inativos,
                total,
                percentual(ativos, total),
                percentual(inativos, total)
        );
    }

    private long valorOuZero(Long valor) {
        return valor == null ? 0L : valor;
    }

    private double percentual(long parte, long total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round(parte * 1000.0 / total) / 10.0;
    }

    private LocalDate inicioDaSemana(LocalDate data) {
        return data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private BigDecimal normalizar(BigDecimal valor) {
        return valor.setScale(3, RoundingMode.HALF_UP);
    }

    private record ConsumoAcumulado(String nome, UnidadeMedidaEnum unidadeMedida, BigDecimal quantidade) {
    }

    private record ChaveSemanaTipoUnidade(LocalDate semana, TipoMovimentacaoEnum tipo, UnidadeMedidaEnum unidadeMedida) {
    }

    private record ChaveSemanaUnidade(LocalDate semana, UnidadeMedidaEnum unidadeMedida) {
    }
}
