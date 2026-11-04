package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class EstoqueSemSaldoException extends RuntimeException {
    public EstoqueSemSaldoException() {
        this("O insumo está sem saldo disponível para registrar uma saída.");
    }

    public EstoqueSemSaldoException(String mensagem) {
        super(mensagem);
    }
}
