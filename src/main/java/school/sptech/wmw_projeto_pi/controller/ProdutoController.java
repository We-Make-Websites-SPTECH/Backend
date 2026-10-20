package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.produto.ProdutoRequestDtoCreate;
import school.sptech.wmw_projeto_pi.dto.produto.ProdutoRequestDtoUpdate;
import school.sptech.wmw_projeto_pi.dto.produto.ProdutoResponseDtoGetAll;
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
    public ResponseEntity<List<ProdutoResponseDtoGetAll>> getAll() {
        List<Produto> produtos = produtoService.getAll();
        return ResponseEntity.status(200).body(ProdutoMapper.toResponseDto(produtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDtoGetAll> getById(@PathVariable Integer id) {
        Produto produto = produtoService.getById(id);
        return ResponseEntity.status(200).body(ProdutoMapper.toResponseDto(produto));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDtoGetAll> create(@Valid @RequestBody ProdutoRequestDtoCreate produtoRequestDto, @RequestHeader("Usuario-Id") Integer idUsuario) {
        // preciso que o front envie o id do usuario pelo header da req. axios enquanto nao tem jwt

        Produto produto = ProdutoMapper.toEntityCreate(produtoRequestDto);
        Produto produtoCadastrado = produtoService.create(produto, idUsuario);
        return ResponseEntity.status(201).body(ProdutoMapper.toResponseDto(produtoCadastrado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDtoGetAll> update(@PathVariable Integer id, @Valid @RequestBody ProdutoRequestDtoUpdate produtoRequestDto, @RequestHeader("Usuario-Id") Integer idUsuario) {
        Produto produto = ProdutoMapper.toEntityUpdate(produtoRequestDto);
        Produto produtoAtualizado = produtoService.update(id, produto, idUsuario);
        return ResponseEntity.status(200).body(ProdutoMapper.toResponseDto(produtoAtualizado));
    }

}
