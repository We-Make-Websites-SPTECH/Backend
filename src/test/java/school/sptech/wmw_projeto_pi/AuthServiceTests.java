package school.sptech.wmw_projeto_pi;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import school.sptech.wmw_projeto_pi.config.PasswordConfig;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioCadastroDTO;
import school.sptech.wmw_projeto_pi.dto.usuario.UsuarioLoginDTO;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.CredenciaisInvalidasException;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;
import school.sptech.wmw_projeto_pi.service.AuthService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AuthServiceTests {
    @Test
    void cadastroArmazenaHashELoginValidaCredenciais() {
        var repository = mock(UsuarioRepository.class);
        var encoder = new PasswordConfig().passwordEncoder();
        var service = new AuthService(repository, encoder);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1);
            when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
            return usuario;
        });
        var cadastro = new UsuarioCadastroDTO();
        cadastro.setNome(" Teste ");
        cadastro.setEmail(" TESTE@EXAMPLE.COM ");
        cadastro.setSenha("SenhaTeste123!");
        var resposta = service.cadastrar(cadastro);
        assertEquals("teste@example.com", resposta.getEmail());
        var usuario = repository.findByEmail(resposta.getEmail()).orElseThrow();
        assertNotEquals(cadastro.getSenha(), usuario.getSenha());
        assertTrue(encoder.matches(cadastro.getSenha(), usuario.getSenha()));
        var login = new UsuarioLoginDTO();
        login.setEmail(" TESTE@EXAMPLE.COM ");
        login.setSenha(cadastro.getSenha());
        assertEquals(1, service.login(login).getId());
        login.setSenha("senha incorreta");
        assertThrows(CredenciaisInvalidasException.class, () -> service.login(login));
        login.setSenha(cadastro.getSenha());
        usuario.setAtivo(false);
        assertThrows(CredenciaisInvalidasException.class, () -> service.login(login));
        usuario.setAtivo(true);
        usuario.setSenha(cadastro.getSenha());
        assertThrows(CredenciaisInvalidasException.class, () -> service.login(login));
        login.setEmail("ausente@example.com");
        assertThrows(CredenciaisInvalidasException.class, () -> service.login(login));
    }

    @Test
    void construtorPreservaUsuarioInativo() {
        var usuario = new Usuario(1, "Teste", "teste@example.com", "senha", false);
        assertFalse(usuario.getAtivo());
    }
}