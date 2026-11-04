package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class SaldoInicialJaRegistradoException extends RuntimeException {

    public SaldoInicialJaRegistradoException() {
        this("Saldo inicial já registrado para o insumo.");
    }

    public SaldoInicialJaRegistradoException(String mensagem) {
        super(mensagem);
    }
}
