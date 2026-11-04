package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import school.sptech.wmw_projeto_pi.entity.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    Optional<Insumo> findInsumoById(Integer id);

    List<Insumo> findByNomeContainingIgnoreCaseOrderByNomeAscIdAsc(String nome);

    Long countByAtivoTrue();

    Long countByAtivoFalse();
}
