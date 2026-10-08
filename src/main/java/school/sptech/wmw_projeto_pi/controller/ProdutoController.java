package school.sptech.wmw_projeto_pi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.mapper.ProdutoMapper;
import school.sptech.wmw_projeto_pi.repository.ProdutoRepository;
import school.sptech.wmw_projeto_pi.service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/produto")

public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final ProdutoService produtoService;


    public ProdutoController(ProdutoRepository produtoRepository, ProdutoService produtoService) {
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> getAll() {
        List<Produto> produtos = produtoService.getAll();
        return ResponseEntity.status(200).body(ProdutoMapper.toResponseDto(produtos));
    }
}
