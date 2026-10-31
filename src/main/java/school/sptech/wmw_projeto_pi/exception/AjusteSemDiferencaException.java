package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class AjusteSemDiferencaException extends RuntimeException {
    public AjusteSemDiferencaException() {
        super("O novo saldo é igual ao saldo atual. Nenhuma movimentação foi registrada.");
    }
}
