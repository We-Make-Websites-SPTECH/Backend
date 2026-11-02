package school.sptech.wmw_projeto_pi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.dto.estoque.MovimentacaoEstoqueResponseDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.enums.TipoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.exception.AjusteSemDiferencaException;
import school.sptech.wmw_projeto_pi.exception.EstoqueInvalidoException;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.UserNotFoundException;
import school.sptech.wmw_projeto_pi.mapper.MovimentacaoEstoqueMapper;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class EstoqueService {
    private final EntityManager entityManager;
    private final InsumoRepository insumoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public EstoqueService(InsumoRepository insumoRepository,
                          MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
                          UsuarioRepository usuarioRepository, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.insumoRepository = insumoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarEntrada(Integer insumoId, BigDecimal quantidadeBruta,
                                                         MotivoMovimentacaoEnum motivo, String observacao,
                                                         Integer usuarioId) {
        validarDecimal(quantidadeBruta, "Quantidade bruta");
        if (quantidadeBruta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EstoqueInvalidoException("A quantidade bruta deve ser maior que zero.");
        }
        if (motivo != MotivoMovimentacaoEnum.COMPRA && motivo != MotivoMovimentacaoEnum.OUTRO) {
            throw new EstoqueInvalidoException("Informe COMPRA ou OUTRO como motivo da entrada.");
        }
        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumoComBloqueio(insumoId);
        Integer percentual = insumo.getPercentualPerda();
        if (percentual == null || percentual < 0 || percentual >= 100) {
            throw new EstoqueInvalidoException("Configure um percentual de perda inteiro entre 0 e 99 no insumo. Perda de 100% não é permitida.");
        }
        BigDecimal quantidadeLiquida = quantidadeBruta
                .multiply(BigDecimal.valueOf(100L - percentual)).movePointLeft(2);
        validarDecimal(quantidadeLiquida, "Quantidade após perda");
        return aplicarMovimentacao(insumo, usuario, quantidadeLiquida, motivo, observacao);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarSaida(Integer insumoId, BigDecimal quantidade,
                                                       MotivoMovimentacaoEnum motivo, String observacao,
                                                       Integer usuarioId) {
        validarDecimal(quantidade, "Quantidade");
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EstoqueInvalidoException("A quantidade deve ser maior que zero.");
        }
        if (motivo == null) {
            throw new EstoqueInvalidoException("Informe um motivo existente para a saída.");
        }
        if (motivo == MotivoMovimentacaoEnum.VENDA || motivo == MotivoMovimentacaoEnum.CANCELAMENTO_VENDA) {
            throw new EstoqueInvalidoException("Este motivo exige vínculo com uma venda e não se aplica a este fluxo.");
        }
        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumoComBloqueio(insumoId);
        if (insumo.getQuantidadeAtual().compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException("O insumo possui saldo negativo preexistente e precisa ser regularizado por ajuste.");
        }
        BigDecimal quantidadeEfetivada = quantidade.min(insumo.getQuantidadeAtual());
        MovimentacaoEstoqueResponseDto resposta;
        if (quantidadeEfetivada.compareTo(BigDecimal.ZERO) == 0) {
            resposta = new MovimentacaoEstoqueResponseDto();
            resposta.setInsumoId(insumoId);
            resposta.setUsuarioId(usuarioId);
            resposta.setTipo(TipoMovimentacaoEnum.SAIDA);
            resposta.setMotivo(motivo);
            resposta.setQuantidade(BigDecimal.ZERO);
            resposta.setSaldoAtual(insumo.getQuantidadeAtual());
            resposta.setObservacao(observacao);
        } else {
            resposta = aplicarMovimentacao(insumo, usuario, quantidadeEfetivada.negate(), motivo, observacao);
        }
        resposta.setQuantidadeSolicitada(quantidade);
        resposta.setQuantidadeEfetivada(quantidadeEfetivada);
        if (quantidade.compareTo(quantidadeEfetivada) > 0) {
            resposta.setAviso("A quantidade solicitada excede o disponível em estoque. O saldo ficou zerado; somente a quantidade disponível foi descontada.");
        }
        return resposta;
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto ajustarSaldo(Integer insumoId, BigDecimal novoSaldo,
                                                     String observacao, Integer usuarioId) {
        validarDecimal(novoSaldo, "Novo saldo");
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException("O novo saldo não pode ser negativo.");
        }
        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumoComBloqueio(insumoId);
        BigDecimal diferenca = novoSaldo.subtract(insumo.getQuantidadeAtual());
        if (diferenca.compareTo(BigDecimal.ZERO) == 0) {
            throw new AjusteSemDiferencaException();
        }
        return aplicarMovimentacao(insumo, usuario, diferenca, MotivoMovimentacaoEnum.AJUSTE, observacao);
    }

    private MovimentacaoEstoqueResponseDto aplicarMovimentacao(Insumo insumo, Usuario usuario,
                                                              BigDecimal diferenca, MotivoMovimentacaoEnum motivo,
                                                              String observacao) {
        BigDecimal quantidade = diferenca.abs();
        BigDecimal novoSaldo = insumo.getQuantidadeAtual().add(diferenca);
        validarDecimal(quantidade, "Quantidade da movimentação");
        validarDecimal(novoSaldo, "Saldo resultante");
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException("O saldo resultante não pode ser negativo.");
        }
        if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
            throw new EstoqueInvalidoException("Uma movimentação deve ter quantidade maior que zero.");
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setInsumo(insumo);
        movimentacao.setUsuario(usuario);
        movimentacao.setMotivo(motivo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao(observacao);
        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            movimentacao.setTipo(TipoMovimentacaoEnum.ENTRADA);
        } else {
            movimentacao.setTipo(TipoMovimentacaoEnum.SAIDA);
        }

        insumo.setQuantidadeAtual(novoSaldo);
        insumo.setUpdatedBy(usuario);
        insumoRepository.save(insumo);
        movimentacaoEstoqueRepository.save(movimentacao);
        return MovimentacaoEstoqueMapper.toResponseDto(movimentacao);
    }

    private Insumo buscarInsumoComBloqueio(Integer insumoId) {
        if (insumoId == null) {
            throw new EstoqueInvalidoException("O insumo é obrigatório.");
        }
        Optional<Insumo> insumo = insumoRepository.findInsumoById(insumoId);
        if (insumo.isEmpty()) {
            throw new InsumoNotFoundException();
        }
        entityManager.refresh(insumo.get(), LockModeType.PESSIMISTIC_WRITE);
        return insumo.get();
    }

    private Usuario buscarUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            throw new EstoqueInvalidoException("O usuário é obrigatório.");
        }
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            throw new UserNotFoundException();
        }
        return usuario.get();
    }

    private void validarDecimal(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new EstoqueInvalidoException(campo + " é obrigatório.");
        }
        BigDecimal valorSemZeros = valor.stripTrailingZeros();
        if (valorSemZeros.scale() > 3 || valor.abs().compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new EstoqueInvalidoException(campo + " deve caber em DECIMAL(12,3), sem arredondamento.");
        }
    }

    private void validarObservacao(String observacao) {
        if (observacao != null && observacao.length() > 255) {
            throw new EstoqueInvalidoException("A observação deve ter no máximo 255 caracteres.");
        }
    }
}
