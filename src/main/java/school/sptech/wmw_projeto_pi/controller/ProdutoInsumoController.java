package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ProdutoInsumoRequestDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ProdutoInsumoUpdateRequestDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ReceitaResponseDto;
import school.sptech.wmw_projeto_pi.service.ProdutoInsumoService;

import java.util.List;

@RestController
@RequestMapping("/receita")
public class ProdutoInsumoController {
    private final ProdutoInsumoService produtoInsumoService;

    public ProdutoInsumoController(ProdutoInsumoService receitaService) {
        this.produtoInsumoService = receitaService;
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResponseDto>> getAllReceitas() {
        List<ReceitaResponseDto> receitas = produtoInsumoService.getAllReceitas();
        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/{produtoId}")
    public ResponseEntity<ReceitaResponseDto> getReceitaByProdutoId(@PathVariable Integer produtoId) {
        ReceitaResponseDto receita = produtoInsumoService.getReceitaByProdutoId(produtoId);
        return ResponseEntity.ok(receita);
    }

    @PostMapping
    public ResponseEntity<ReceitaResponseDto> create(@Valid @RequestBody ProdutoInsumoRequestDto receitaRequestDto,  @RequestHeader("Usuario-Id") Integer idUsuario) {
        ReceitaResponseDto receitaCadastrada = produtoInsumoService.create(receitaRequestDto, idUsuario);
        return ResponseEntity.status(201).body(receitaCadastrada);
    }

    @PutMapping("/{produtoId}")
    public ResponseEntity<ReceitaResponseDto> updateReceita(@PathVariable Integer produtoId, @Valid @RequestBody ProdutoInsumoUpdateRequestDto receitaRequest, @RequestHeader("Usuario-Id") Integer idUsuario) {
        ReceitaResponseDto receitaAtualizada =produtoInsumoService.updateReceita(produtoId, receitaRequest, idUsuario);
        return ResponseEntity.status(200).body(receitaAtualizada);
    }
}
