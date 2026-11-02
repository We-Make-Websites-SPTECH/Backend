package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.insumo.InsumoRequestDtoCreate;
import school.sptech.wmw_projeto_pi.dto.insumo.InsumoRequestDtoUpdate;
import school.sptech.wmw_projeto_pi.dto.insumo.InsumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.insumo.MovimentacaoResponseDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.mapper.InsumoMapper;
import school.sptech.wmw_projeto_pi.mapper.MovimentacaoEstoqueMapper;
import school.sptech.wmw_projeto_pi.service.InsumoService;

import java.util.List;

@RestController
@RequestMapping("/insumo")
public class InsumoController {
    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponseDto>> getAll(
            @RequestHeader("Usuario-Id") Integer idUsuario,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(required = false) Boolean ativo) {
        insumoService.validarUsuario(idUsuario);
        List<Insumo> insumos = insumoService.getAll(busca, ativo);

        return ResponseEntity.status(200).body(InsumoMapper.toResponseDto(insumos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDto> getById(@PathVariable Integer id, @RequestHeader("Usuario-Id") Integer idUsuario) {
        insumoService.validarUsuario(idUsuario);
        Insumo insumo = insumoService.getById(id);

        return ResponseEntity.status(200).body(InsumoMapper.toResponseDto(insumo));
    }

    @PostMapping
    public ResponseEntity<InsumoResponseDto> create(
            @Valid @RequestBody InsumoRequestDtoCreate insumoRequestDto,
            @RequestHeader("Usuario-Id") Integer idUsuario) {
        Insumo insumo = InsumoMapper.toEntityCreate(insumoRequestDto);
        Insumo insumoCadastrado = insumoService.create(insumo, idUsuario);

        return ResponseEntity.status(201).body(InsumoMapper.toResponseDto(insumoCadastrado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody InsumoRequestDtoUpdate insumoRequestDto,
            @RequestHeader("Usuario-Id") Integer idUsuario) {
        Insumo insumo = InsumoMapper.toEntityUpdate(insumoRequestDto);
        Insumo insumoAtualizado = insumoService.update(id, insumo, idUsuario);

        return ResponseEntity.status(200).body(InsumoMapper.toResponseDto(insumoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Integer id, @RequestHeader("Usuario-Id") Integer idUsuario) {
        insumoService.validarUsuario(idUsuario);
        insumoService.desativar(id);

        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoResponseDto>> getMovimentacoes(
            @PathVariable Integer id, @RequestHeader("Usuario-Id") Integer idUsuario) {
        insumoService.validarUsuario(idUsuario);
        List<MovimentacaoEstoque> movimentacoes = insumoService.getMovimentacoes(id);

        return ResponseEntity.status(200).body(MovimentacaoEstoqueMapper.toResponseDto(movimentacoes));
    }
}
