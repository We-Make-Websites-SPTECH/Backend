package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        this("E-mail ou senha inválidos.");
    }

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
