package school.sptech.wmw_projeto_pi.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioCadastroDTO;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioLoginDTO;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioResponseDTO;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.CredenciaisInvalidasException;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {
        String email = dto.getEmail()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }


        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(dto.getSenha());
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return UsuarioResponseDTO.fromEntity(usuarioSalvo);
    }

    public UsuarioResponseDTO login(UsuarioLoginDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || usuario.getSenha() == null
                || !usuario.getSenha().equals(dto.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return UsuarioResponseDTO.fromEntity(usuario);
    }

}
