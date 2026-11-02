package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Insumo desativado.")
public class InsumoDesativadoException extends RuntimeException {

    public InsumoDesativadoException() {
        super("Insumo desativado.");
    }
}
