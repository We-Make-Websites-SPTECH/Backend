package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SaldoInsumoInvalidoException extends RuntimeException {

    public SaldoInsumoInvalidoException() {
        this("Saldo deve ser não negativo, até 999999999.999, com no máximo três casas decimais.");
    }

    public SaldoInsumoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
