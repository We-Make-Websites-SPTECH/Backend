package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;

import java.util.List;

public interface ImportacaoLinhaRepository extends JpaRepository<ImportacaoLinha, Long> {

    List<ImportacaoLinha> findByImportacaoIdOrderByNumeroLinhaAsc(Long id);

    long countByImportacaoIdAndStatus(Long id, StatusImportacaoLinhaEnum status);
}
