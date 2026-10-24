package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
}
