package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Saldo inicial já registrado para o insumo.")
public class SaldoInicialJaRegistradoException extends RuntimeException {

    public SaldoInicialJaRegistradoException() {
        super("Saldo inicial já registrado para o insumo.");
    }
}
