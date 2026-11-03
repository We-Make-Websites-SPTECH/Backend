package school.sptech.wmw_projeto_pi.repository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;
public interface ImportacaoLinhaRepository extends JpaRepository<ImportacaoLinha,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from ImportacaoLinha l where l.id=:id")
    Optional<ImportacaoLinha> buscarComBloqueio(@Param("id") Long id);
    Page<ImportacaoLinha> findByImportacaoId(Long id, Pageable pageable);
    long countByImportacaoIdAndStatus(Long id, StatusImportacaoLinhaEnum status);
}
