package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.estoque.AjusteEstoqueRequestDto;
import school.sptech.wmw_projeto_pi.dto.estoque.EntradaEstoqueRequestDto;
import school.sptech.wmw_projeto_pi.dto.estoque.MovimentacaoEstoqueResponseDto;
import school.sptech.wmw_projeto_pi.dto.estoque.SaidaEstoqueRequestDto;
import school.sptech.wmw_projeto_pi.service.EstoqueService;

@RestController
@RequestMapping("/estoque")
@CrossOrigin(origins = "http://localhost:5173")
public class EstoqueController {
    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/entradas")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> registrarEntrada(
            @Valid @RequestBody EntradaEstoqueRequestDto request,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        MovimentacaoEstoqueResponseDto resposta = estoqueService.registrarEntrada(
                request.getInsumoId(), request.getQuantidadeBruta(), request.getMotivo(),
                request.getObservacao(), usuarioId);
        return ResponseEntity.status(201).body(resposta);
    }

    @PostMapping("/saidas")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> registrarSaida(
            @Valid @RequestBody SaidaEstoqueRequestDto request,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        MovimentacaoEstoqueResponseDto resposta = estoqueService.registrarSaida(
                request.getInsumoId(), request.getQuantidade(), request.getMotivo(),
                request.getObservacao(), usuarioId);
        return ResponseEntity.status(resposta.getId() == null ? 200 : 201).body(resposta);
    }

    @PostMapping("/ajustes")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> ajustarSaldo(
            @Valid @RequestBody AjusteEstoqueRequestDto request,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        MovimentacaoEstoqueResponseDto resposta = estoqueService.ajustarSaldo(
                request.getInsumoId(), request.getNovoSaldo(), request.getObservacao(), usuarioId);
        return ResponseEntity.status(201).body(resposta);
    }
}
