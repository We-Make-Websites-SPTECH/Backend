package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoPersonalizacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.service.ProcessamentoConsumoService;
import school.sptech.wmw_projeto_pi.service.RegistroConsumoService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/importacoes")
@CrossOrigin(origins = "http://localhost:5173")
public class ImportacaoController {
    private final RegistroConsumoService service;
    private final ProcessamentoConsumoService processamento;

    public ImportacaoController(RegistroConsumoService service, ProcessamentoConsumoService processamento) {
        this.service = service;
        this.processamento = processamento;
    }

    @PostMapping(value = "/txt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacaoResumoResponseDto> upload(
            @RequestParam MultipartFile arquivo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia,
            @RequestHeader("Usuario-Id") Integer usuarioId) throws IOException {
        return ResponseEntity.status(201).body(
                service.receber(arquivo.getOriginalFilename(), arquivo.getBytes(), dataReferencia, usuarioId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ImportacaoResumoResponseDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportacaoResumoResponseDto> consultar(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultar(id));
    }

    @GetMapping("/{id}/linhas")
    public ResponseEntity<List<ImportacaoLinhaResponseDto>> linhas(@PathVariable Long id) {
        return ResponseEntity.ok(service.linhas(id));
    }

    @PatchMapping("/{id}/linhas/{linhaId}/ingredientes")
    public ResponseEntity<ImportacaoLinhaResponseDto> personalizar(
            @PathVariable Long id,
            @PathVariable Long linhaId,
            @Valid @RequestBody ConsumoPersonalizacaoRequestDto dados,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.ok(processamento.personalizar(id, linhaId, dados, usuarioId));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<List<ImportacaoResultadoResponseDto>> confirmar(
            @PathVariable Long id,
            @Valid @RequestBody ImportacaoConfirmacaoRequestDto pedido,
            @RequestHeader("Usuario-Id") Integer usuarioId) {
        return ResponseEntity.ok(service.confirmar(id, pedido, usuarioId));
    }

}
