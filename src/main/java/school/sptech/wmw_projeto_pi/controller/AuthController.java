package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioCadastroDTO;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioLoginDTO;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioResponseDTO;
import school.sptech.wmw_projeto_pi.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        UsuarioResponseDTO resposta = authService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody UsuarioLoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}