package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unidade de medida deve ser g, ml ou un.")
public class UnidadeMedidaInvalidaException extends RuntimeException {

    public UnidadeMedidaInvalidaException() {
        super("Unidade de medida deve ser g, ml ou un.");
    }
}
