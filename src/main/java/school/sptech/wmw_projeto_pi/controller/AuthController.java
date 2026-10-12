package school.sptech.wmw_projeto_pi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.wmw_projeto_pi.dto.UsuarioCadastroDTO;
import school.sptech.wmw_projeto_pi.dto.UsuarioFullDTO;
import school.sptech.wmw_projeto_pi.entity.Usuario;

@RestController
public class AuthController {

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioFullDTO> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        if(Usuario)
    }
}
