package school.sptech.wmw_projeto_pi.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.exception.EstoqueInvalidoException;
import school.sptech.wmw_projeto_pi.exception.InsumoDesativadoException;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.SaldoInicialJaRegistradoException;
import school.sptech.wmw_projeto_pi.exception.SaldoInsumoInvalidoException;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;

import java.math.BigDecimal;

@Service
public class MovimentacaoEstoqueService {
    private final InsumoRepository insumoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public MovimentacaoEstoqueService(InsumoRepository insumoRepository,
                                     MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @Transactional
    public void registrar(Integer idInsumo, Usuario usuario, BigDecimal diferencaSaldo, boolean saldoInicial) {
        if (!saldoInicial && diferencaSaldo != null && diferencaSaldo.signum() == 0) {
            Insumo insumo = insumoRepository.findInsumoById(idInsumo).orElseThrow(InsumoNotFoundException::new);
            if (!Boolean.TRUE.equals(insumo.getAtivo())) throw new InsumoDesativadoException();
            return;
        }

        MotivoMovimentacaoEnum motivo = saldoInicial
                ? MotivoMovimentacaoEnum.SALDO_INICIAL : MotivoMovimentacaoEnum.AJUSTE;
        registrar(idInsumo, usuario, diferencaSaldo, motivo, null);
    }

    // Cadastro, edição e estoque compartilham a gravação do histórico e do saldo.
    @Transactional
    public MovimentacaoEstoque registrar(Integer idInsumo, Usuario usuario, BigDecimal diferencaSaldo,
                                        MotivoMovimentacaoEnum motivo, String observacao) {
        Insumo insumo = insumoRepository.findInsumoById(idInsumo).orElseThrow(InsumoNotFoundException::new);
        if (!Boolean.TRUE.equals(insumo.getAtivo())) throw new InsumoDesativadoException();

        boolean saldoInicial = motivo == MotivoMovimentacaoEnum.SALDO_INICIAL;
        if (motivo == null || motivo == MotivoMovimentacaoEnum.VENDA
                || motivo == MotivoMovimentacaoEnum.CANCELAMENTO_VENDA) {
            throw new EstoqueInvalidoException("Motivo inválido para uma movimentação sem vínculo com venda.");
        }
        if (observacao != null && observacao.length() > 255) {
            throw new EstoqueInvalidoException("A observação deve ter no máximo 255 caracteres.");
        }
        if (saldoInicial && movimentacaoEstoqueRepository.existsByInsumoId(idInsumo)) {
            throw new SaldoInicialJaRegistradoException();
        }
        if (diferencaSaldo == null) throw new SaldoInsumoInvalidoException();

        BigDecimal novoSaldo = insumo.getQuantidadeAtual().add(diferencaSaldo);
        validarSaldo(novoSaldo, diferencaSaldo, saldoInicial);
        if (!saldoInicial && diferencaSaldo.signum() == 0) {
            throw new EstoqueInvalidoException("Uma movimentação deve ter quantidade maior que zero.");
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(diferencaSaldo.abs());
        movimentacao.setMotivo(motivo);
        movimentacao.setObservacao(observacao);
        if (diferencaSaldo.signum() < 0) {
            movimentacao.setTipo(TipoMovimentacaoEnum.SAIDA);
        } else {
            movimentacao.setTipo(TipoMovimentacaoEnum.ENTRADA);
        }

        insumo.setQuantidadeAtual(novoSaldo);
        insumo.setUpdatedBy(usuario);
        insumoRepository.save(insumo);
        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    private void validarSaldo(BigDecimal novoSaldo, BigDecimal diferencaSaldo, boolean saldoInicial) {
        if (novoSaldo.signum() < 0 || novoSaldo.compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new SaldoInsumoInvalidoException();
        }
        if (diferencaSaldo.stripTrailingZeros().scale() > 3
                || diferencaSaldo.abs().compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new SaldoInsumoInvalidoException();
        }
        if (saldoInicial && diferencaSaldo.signum() < 0) {
            throw new SaldoInsumoInvalidoException();
        }
    }
}
