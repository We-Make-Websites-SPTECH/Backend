package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class UsuarioInexistenteOuInativoException extends RuntimeException {

    public UsuarioInexistenteOuInativoException() {
        this("Usuário inexistente ou inativo.");
    }

    public UsuarioInexistenteOuInativoException(String mensagem) {
        super(mensagem);
    }
}
