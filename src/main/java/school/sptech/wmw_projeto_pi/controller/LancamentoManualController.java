package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoPersonalizacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualPreviaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualRequestDto;
import school.sptech.wmw_projeto_pi.service.LancamentoManualService;

import java.util.List;

@RestController
@RequestMapping("/lancamentos/manuais")
@CrossOrigin(origins = "http://localhost:5173")
public class LancamentoManualController {
    private final LancamentoManualService service;

    public LancamentoManualController(LancamentoManualService service) {
        this.service = service;
    }

    @PostMapping("/previa")
    public ResponseEntity<LancamentoManualPreviaResponseDto> preparar(
            @Valid @RequestBody LancamentoManualRequestDto pedido,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.status(201).body(service.preparar(pedido, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoManualPreviaResponseDto> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultar(id));
    }

    @PatchMapping("/{id}/linhas/{linhaId}/ingredientes")
    public ResponseEntity<ImportacaoLinhaResponseDto> personalizar(
            @PathVariable Long id,
            @PathVariable Long linhaId,
            @Valid @RequestBody ConsumoPersonalizacaoRequestDto pedido,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.ok(service.personalizar(id, linhaId, pedido, usuarioId));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<List<ImportacaoResultadoResponseDto>> confirmar(
            @PathVariable Long id,
            @Valid @RequestBody ImportacaoConfirmacaoRequestDto pedido,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.ok(service.confirmar(id, pedido, usuarioId));
    }
}
