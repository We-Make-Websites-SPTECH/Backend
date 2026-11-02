package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoPeriodoDto;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("""
            SELECT new school.sptech.wmw_projeto_pi.dto.dashboard.MovimentacaoPeriodoDto(
                m.insumo.id,
                m.insumo.nome,
                m.insumo.unidadeMedida,
                m.insumo.ativo,
                m.tipo,
                m.motivo,
                m.quantidade,
                m.dataHora
            )
            FROM MovimentacaoEstoque m
            WHERE m.dataHora >= :inicio AND m.dataHora < :fim
            """)
    List<MovimentacaoPeriodoDto> buscarNoPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
    List<MovimentacaoEstoque> findByInsumoIdOrderByDataHoraAscIdAsc(Integer insumoId);

    boolean existsByInsumoIdAndQuantidadeGreaterThan(Integer insumoId, BigDecimal quantidade);

    boolean existsByInsumoId(Integer insumoId);
}
