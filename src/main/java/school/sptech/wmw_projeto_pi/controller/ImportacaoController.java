package school.sptech.wmw_projeto_pi.controller;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import school.sptech.wmw_projeto_pi.service.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;

@RestController
@RequestMapping("/importacoes")
@CrossOrigin(origins="http://localhost:5173")
public class ImportacaoController {
    private final ImportacaoService service;
    private final ImportacaoLinhaService linhas;
    public ImportacaoController(ImportacaoService service,ImportacaoLinhaService linhas) { this.service=service;this.linhas=linhas; }
    @PostMapping(value="/txt",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resumo> upload(@RequestParam MultipartFile arquivo,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate dataReferencia,
            @RequestHeader("Usuario-Id") Integer usuarioId) throws IOException {
        return ResponseEntity.status(201).body(service.receber(arquivo.getOriginalFilename(),arquivo.getBytes(),dataReferencia,usuarioId));
    }
    @GetMapping public Page<Resumo> listar(@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho) {
        validarPagina(pagina,tamanho);return service.listar(pagina,tamanho);
    }
    @GetMapping("/{id}") public Resumo consultar(@PathVariable Long id) { return service.consultar(id); }
    @GetMapping("/{id}/linhas") public Page<Linha> linhas(@PathVariable Long id,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho) {
        validarPagina(pagina,tamanho);return service.linhas(id,pagina,tamanho);
    }
    @PatchMapping("/{id}/linhas/{linhaId}") public Linha corrigir(@PathVariable Long id,@PathVariable Long linhaId,
            @Valid @RequestBody Correcao dados,@RequestHeader("Usuario-Id") Integer usuarioId) {
        return linhas.corrigir(id,linhaId,dados,usuarioId);
    }
    @PostMapping("/{id}/confirmar") public List<Resultado> confirmar(@PathVariable Long id,@Valid @RequestBody Confirmacao pedido,
            @RequestHeader("Usuario-Id") Integer usuarioId) { return service.confirmar(id,pedido,usuarioId); }
    private void validarPagina(int pagina,int tamanho) {
        if(pagina<0 || tamanho<1 || tamanho>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Página >= 0 e tamanho entre 1 e 100.");
    }
}
