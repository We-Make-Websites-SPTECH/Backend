package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;

import java.util.List;

public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {

    List<Importacao> findByStatusOrderByIdDesc(StatusImportacaoEnum status);
}
