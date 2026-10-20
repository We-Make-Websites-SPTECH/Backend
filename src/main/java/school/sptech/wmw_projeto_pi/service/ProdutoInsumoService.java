package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.exception.ProductNotFoundException;
import school.sptech.wmw_projeto_pi.exception.ReceitaNotFoundException;
import school.sptech.wmw_projeto_pi.repository.ProdutoInsumoRepository;

import java.util.Optional;

@Service
public class ProdutoInsumoService {
    private final ProdutoInsumoRepository produtoInsumoRepository;

    public ProdutoInsumoService(ProdutoInsumoRepository produtoInsumoRepository) {
        this.produtoInsumoRepository = produtoInsumoRepository;
    }

    public ProdutoInsumo getById(Integer id) {
        Optional<ProdutoInsumo> produtoInsumoOptional = produtoInsumoRepository.findById(id);
        if (produtoInsumoOptional.isEmpty()) throw new ReceitaNotFoundException();

        return produtoInsumoOptional.get();
    }
}
