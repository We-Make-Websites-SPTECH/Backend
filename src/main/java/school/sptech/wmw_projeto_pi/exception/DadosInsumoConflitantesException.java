package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Dados do insumo em conflito com um registro existente.")
public class DadosInsumoConflitantesException extends RuntimeException {

    public DadosInsumoConflitantesException() {
        super("Dados do insumo em conflito com um registro existente.");
    }
}
