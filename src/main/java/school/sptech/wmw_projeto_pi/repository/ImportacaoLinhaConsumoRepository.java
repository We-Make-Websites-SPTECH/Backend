package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;

import java.util.List;

public interface ImportacaoLinhaConsumoRepository extends JpaRepository<ImportacaoLinhaConsumo, Long> {
    List<ImportacaoLinhaConsumo> findByLinhaIdOrderByInsumoIdAsc(Long linhaId);
}
