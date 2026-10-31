package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EstoqueInvalidoException extends RuntimeException {
    public EstoqueInvalidoException(String mensagem) {
        super(mensagem);
    }
}
