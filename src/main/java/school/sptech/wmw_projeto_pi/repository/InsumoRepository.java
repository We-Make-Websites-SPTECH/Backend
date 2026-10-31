package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;
import school.sptech.wmw_projeto_pi.entity.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Insumo> findInsumoById(Integer id);
}
