package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CredenciaisInvalidasException extends ResponseStatusException {

    public CredenciaisInvalidasException() {
        super(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }
}
