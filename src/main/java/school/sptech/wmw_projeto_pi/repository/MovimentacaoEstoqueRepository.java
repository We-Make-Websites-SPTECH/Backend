package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;

import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    List<MovimentacaoEstoque> findByInsumoIdOrderByDataHoraAscIdAsc(Integer insumoId);

    boolean existsByInsumoIdAndQuantidadeGreaterThan(Integer insumoId, BigDecimal quantidade);

    boolean existsByInsumoId(Integer insumoId);
}
