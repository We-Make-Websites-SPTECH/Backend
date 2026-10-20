package school.sptech.wmw_projeto_pi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ProdutoInsumoResponseDto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.mapper.ProdutoInsumoMapper;
import school.sptech.wmw_projeto_pi.service.ProdutoInsumoService;

@RestController
@RequestMapping("/receita")
public class ProdutoInsumoController {
    private final ProdutoInsumoService produtoInsumoService;

    public ProdutoInsumoController(ProdutoInsumoService receitaService) {
        this.produtoInsumoService = receitaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoInsumoResponseDto> getById(@PathVariable Integer id) {
        ProdutoInsumo produtoInsumo = produtoInsumoService.getById(id);
        return ResponseEntity.status(200).body(ProdutoInsumoMapper.toResponseDto(produtoInsumo));
    }


}
