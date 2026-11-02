package school.sptech.wmw_projeto_pi.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.InsumoDesativadoException;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.SaldoInicialJaRegistradoException;
import school.sptech.wmw_projeto_pi.exception.SaldoInsumoInvalidoException;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class MovimentacaoEstoqueService {
    private final InsumoRepository insumoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public MovimentacaoEstoqueService(InsumoRepository insumoRepository,
                                     MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    // Histórico e saldo são atualizados juntos, na mesma transação.
    @Transactional
    public void registrar(Integer idInsumo, Usuario usuario, BigDecimal diferencaSaldo, boolean saldoInicial) {
        Optional<Insumo> insumoOptional = insumoRepository.buscarParaAtualizar(idInsumo);
        if (insumoOptional.isEmpty()) throw new InsumoNotFoundException();

        Insumo insumo = insumoOptional.get();
        if (!Boolean.TRUE.equals(insumo.getAtivo())) throw new InsumoDesativadoException();

        if (saldoInicial && movimentacaoEstoqueRepository.existsByInsumoId(idInsumo)) {
            throw new SaldoInicialJaRegistradoException();
        }

        BigDecimal novoSaldo = insumo.getQuantidadeAtual().add(diferencaSaldo);
        validarSaldo(novoSaldo, diferencaSaldo, saldoInicial);

        if (!saldoInicial && diferencaSaldo.signum() == 0) {
            return;
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(diferencaSaldo.abs());

        if (diferencaSaldo.signum() < 0) {
            movimentacao.setTipo("SAIDA");
        } else {
            movimentacao.setTipo("ENTRADA");
        }

        if (saldoInicial) {
            movimentacao.setMotivo("SALDO_INICIAL");
        } else {
            movimentacao.setMotivo("AJUSTE");
        }

        movimentacaoEstoqueRepository.save(movimentacao);
        insumo.setQuantidadeAtual(novoSaldo);
        insumoRepository.save(insumo);
    }

    // VALIDAÇÕES

    private void validarSaldo(BigDecimal novoSaldo, BigDecimal diferencaSaldo, boolean saldoInicial) {
        if (novoSaldo.signum() < 0 || novoSaldo.compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new SaldoInsumoInvalidoException();
        }

        if (diferencaSaldo.stripTrailingZeros().scale() > 3) {
            throw new SaldoInsumoInvalidoException();
        }

        if (saldoInicial && diferencaSaldo.signum() < 0) {
            throw new SaldoInsumoInvalidoException();
        }
    }
}
