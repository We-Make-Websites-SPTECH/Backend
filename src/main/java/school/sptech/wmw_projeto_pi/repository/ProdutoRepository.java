package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
