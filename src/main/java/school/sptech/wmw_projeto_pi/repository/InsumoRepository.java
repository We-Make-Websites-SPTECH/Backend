package school.sptech.wmw_projeto_pi.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.wmw_projeto_pi.entity.Insumo;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    List<Insumo> findByNomeContainingIgnoreCaseOrderByNomeAscIdAsc(String nome);

    // Evita que duas operações alterem o saldo do mesmo insumo ao mesmo tempo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Insumo i where i.id = :id")
    Optional<Insumo> buscarParaAtualizar(@Param("id") Integer id);
}
