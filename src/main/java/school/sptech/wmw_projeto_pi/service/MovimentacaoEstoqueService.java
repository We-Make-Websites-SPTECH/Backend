package school.sptech.wmw_projeto_pi.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;
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
import java.util.Optional;

@Service
public class MovimentacaoEstoqueService {
    private final InsumoRepository insumoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final BigDecimal quantidadeMaximaEstoque = new BigDecimal("999999999.999");

    public MovimentacaoEstoqueService(InsumoRepository insumoRepository,
                                     MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @Transactional
    public void registrarSaldoInicial(Integer idInsumo, Usuario usuario, BigDecimal saldoInicial) {
        registrar(idInsumo, usuario, saldoInicial, MotivoMovimentacaoEnum.SALDO_INICIAL, null);
    }

    @Transactional
    public MovimentacaoEstoque registrar(Integer idInsumo, Usuario usuario, BigDecimal diferencaSaldo,
                                        MotivoMovimentacaoEnum motivo, String observacao) {
        return registrar(idInsumo, usuario, diferencaSaldo, motivo, observacao, null);
    }

    @Transactional
    public MovimentacaoEstoque registrar(
            Integer idInsumo, Usuario usuario, BigDecimal diferencaSaldo,
            MotivoMovimentacaoEnum motivo, String observacao, ImportacaoLinhaConsumo consumo) {
        Optional<Insumo> insumoOptional = insumoRepository.findInsumoById(idInsumo);

        if (insumoOptional.isEmpty()) {
            throw new InsumoNotFoundException();
        }

        Insumo insumo = insumoOptional.get();

        if (!Boolean.TRUE.equals(insumo.getAtivo())) {
            throw new InsumoDesativadoException();
        }

        Boolean saldoInicial = motivo == MotivoMovimentacaoEnum.SALDO_INICIAL;

        if (motivo == null || motivo == MotivoMovimentacaoEnum.VENDA
                || motivo == MotivoMovimentacaoEnum.CANCELAMENTO_VENDA) {
            throw new EstoqueInvalidoException("Motivo inválido para uma movimentação sem vínculo com venda.");
        }

        if (observacao != null && observacao.length() > 255) {
            throw new EstoqueInvalidoException("A observação deve ter no máximo 255 caracteres.");
        }

        if (motivo == MotivoMovimentacaoEnum.CONSUMO_PRODUTO) {
            if (consumo == null || consumo.getId() == null
                    || !consumo.getInsumo().getId().equals(idInsumo)
                    || diferencaSaldo == null || diferencaSaldo.signum() >= 0) {
                throw new EstoqueInvalidoException("Consumo exige vínculo com o insumo e movimentação de saída.");
            }
        } else if (consumo != null) {
            throw new EstoqueInvalidoException("Vínculo de consumo somente é permitido para CONSUMO_PRODUTO.");
        }

        if (saldoInicial && movimentacaoEstoqueRepository.existsByInsumoId(idInsumo)) {
            throw new SaldoInicialJaRegistradoException();
        }

        if (diferencaSaldo == null) {
            throw new SaldoInsumoInvalidoException();
        }

        BigDecimal novoSaldo = insumo.getQuantidadeAtual().add(diferencaSaldo);
        validarSaldo(novoSaldo, diferencaSaldo, saldoInicial);

        if (!saldoInicial && diferencaSaldo.signum() == 0) {
            throw new EstoqueInvalidoException("Uma movimentação deve ter quantidade maior que zero.");
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setConsumoImportacao(consumo);
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

    private void validarSaldo(BigDecimal novoSaldo, BigDecimal diferencaSaldo, Boolean saldoInicial) {
        if (novoSaldo.signum() < 0 || novoSaldo.compareTo(quantidadeMaximaEstoque) > 0
                || novoSaldo.stripTrailingZeros().scale() > 3) {
            throw new SaldoInsumoInvalidoException();
        }

        if (diferencaSaldo.stripTrailingZeros().scale() > 3
                || diferencaSaldo.abs().compareTo(quantidadeMaximaEstoque) > 0) {
            throw new SaldoInsumoInvalidoException();
        }

        if (saldoInicial && diferencaSaldo.signum() < 0) {
            throw new SaldoInsumoInvalidoException();
        }
    }
}
