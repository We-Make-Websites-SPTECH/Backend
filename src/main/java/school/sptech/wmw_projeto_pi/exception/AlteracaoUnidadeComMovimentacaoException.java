package school.sptech.wmw_projeto_pi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Unidade não pode mudar após movimentação de saldo.")
public class AlteracaoUnidadeComMovimentacaoException extends RuntimeException {

    public AlteracaoUnidadeComMovimentacaoException() {
        super("Unidade não pode mudar após movimentação de saldo.");
    }
}
