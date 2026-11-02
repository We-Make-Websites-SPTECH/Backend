package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Saldo deve ser não negativo, até 999999999.999, com no máximo três casas decimais.")
public class SaldoInsumoInvalidoException extends RuntimeException {

    public SaldoInsumoInvalidoException() {
        super("Saldo deve ser não negativo, até 999999999.999, com no máximo três casas decimais.");
    }
}
