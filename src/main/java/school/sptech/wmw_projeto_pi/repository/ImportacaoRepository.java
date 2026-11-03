package school.sptech.wmw_projeto_pi.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;

import java.util.List;
import java.util.Optional;

public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Importacao> findWithLockById(Long id);

    List<Importacao> findByStatusOrderByIdDesc(StatusImportacaoEnum status);
}
