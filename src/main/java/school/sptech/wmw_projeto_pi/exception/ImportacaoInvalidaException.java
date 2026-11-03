package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ImportacaoInvalidaException extends RuntimeException {
    public ImportacaoInvalidaException() {
        this("Dados de importação inválidos.");
    }

    public ImportacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
