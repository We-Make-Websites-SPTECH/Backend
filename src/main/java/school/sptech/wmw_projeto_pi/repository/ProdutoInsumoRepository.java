package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;

import java.util.List;

public interface ProdutoInsumoRepository extends JpaRepository<ProdutoInsumo, Integer> {
    List<ProdutoInsumo> findByProdutoId(Integer produtoId);
    List<ProdutoInsumo> findAll();
}


