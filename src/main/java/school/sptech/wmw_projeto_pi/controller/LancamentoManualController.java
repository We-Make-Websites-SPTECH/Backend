package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import school.sptech.wmw_projeto_pi.service.LancamentoManualService;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualDtos.*;

@RestController
@RequestMapping("/lancamentos/manuais")
@CrossOrigin(origins="http://localhost:5173")
public class LancamentoManualController {
    private final LancamentoManualService service;
    public LancamentoManualController(LancamentoManualService service) { this.service=service; }
    @PostMapping("/previa")
    public ResponseEntity<Previa> preparar(@Valid @RequestBody Pedido pedido,@RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.status(201).body(service.preparar(pedido,usuarioId));
    }
    @GetMapping("/{id}")
    public Previa consultar(@PathVariable Long id) { return service.consultar(id); }
    @PatchMapping("/{id}/linhas/{linhaId}")
    public Linha corrigir(@PathVariable Long id,@PathVariable Long linhaId,@Valid @RequestBody CorrecaoManual pedido,
                          @RequestHeader("Usuario-Id") Integer usuarioId) {
        return service.corrigir(id,linhaId,pedido,usuarioId);
    }
    @PostMapping("/{id}/confirmar")
    public List<Resultado> confirmar(@PathVariable Long id,@Valid @RequestBody Confirmacao pedido,
                                    @RequestHeader("Usuario-Id") Integer usuarioId) {
        return service.confirmar(id,pedido,usuarioId);
    }
}
