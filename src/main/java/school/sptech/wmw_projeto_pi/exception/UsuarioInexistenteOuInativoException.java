package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Usuário inexistente ou inativo.")
public class UsuarioInexistenteOuInativoException extends RuntimeException {

    public UsuarioInexistenteOuInativoException() {
        super("Usuário inexistente ou inativo.");
    }
}
