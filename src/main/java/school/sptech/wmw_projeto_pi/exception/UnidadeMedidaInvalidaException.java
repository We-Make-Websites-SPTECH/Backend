package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnidadeMedidaInvalidaException extends RuntimeException {

    public UnidadeMedidaInvalidaException() {
        this("Unidade de medida deve ser g ou un.");
    }

    public UnidadeMedidaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
