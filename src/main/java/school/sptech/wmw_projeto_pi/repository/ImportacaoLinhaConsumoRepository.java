package school.sptech.wmw_projeto_pi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;
public interface ImportacaoLinhaConsumoRepository extends JpaRepository<ImportacaoLinhaConsumo,Long> {
    List<ImportacaoLinhaConsumo> findByLinhaIdOrderByInsumoIdAsc(Long linhaId);
}
