package school.sptech.wmw_projeto_pi.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

import java.util.List;
import java.util.Optional;

public interface ImportacaoLinhaRepository extends JpaRepository<ImportacaoLinha, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ImportacaoLinha> findWithLockById(Long id);

    List<ImportacaoLinha> findByImportacaoIdOrderByNumeroLinhaAsc(Long id);

    long countByImportacaoIdAndStatus(Long id, StatusImportacaoLinhaEnum status);
}
