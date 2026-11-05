package school.sptech.wmw_projeto_pi.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.dto.estoque.MovimentacaoEstoqueResponseDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.MotivoMovimentacaoEnum;
import school.sptech.wmw_projeto_pi.exception.AjusteSemDiferencaException;
import school.sptech.wmw_projeto_pi.exception.EstoqueInvalidoException;
import school.sptech.wmw_projeto_pi.exception.EstoqueSemSaldoException;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.InsumoDesativadoException;
import school.sptech.wmw_projeto_pi.exception.UserNotFoundException;
import school.sptech.wmw_projeto_pi.mapper.MovimentacaoEstoqueMapper;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class EstoqueService {
    private final InsumoRepository insumoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final UsuarioRepository usuarioRepository;
    private final BigDecimal quantidadeMaximaEstoque = new BigDecimal("999999999.999");

    public EstoqueService(
            InsumoRepository insumoRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            UsuarioRepository usuarioRepository) {
        this.insumoRepository = insumoRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarEntrada(
            Integer insumoId, BigDecimal quantidadeBruta, MotivoMovimentacaoEnum motivo,
            String observacao, Integer usuarioId)
    {
        validarPrecisaoQuantidade(quantidadeBruta, "Quantidade bruta");

        if (quantidadeBruta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EstoqueInvalidoException("A quantidade bruta deve ser maior que zero.");
        }

        if (motivo != MotivoMovimentacaoEnum.COMPRA && motivo != MotivoMovimentacaoEnum.OUTRO) {
            throw new EstoqueInvalidoException("Informe COMPRA ou OUTRO como motivo da entrada.");
        }

        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumo(insumoId);
        Integer percentualPerda = insumo.getPercentualPerda();

        if (percentualPerda == null || percentualPerda < 0 || percentualPerda >= 100) {
            throw new EstoqueInvalidoException(
                    "Configure um percentual de perda inteiro entre 0 e 99 no insumo. Perda de 100% não é permitida."
            );
        }

        BigDecimal quantidadeLiquida = calcularQuantidadeLiquida(quantidadeBruta, percentualPerda);

        validarPrecisaoQuantidade(quantidadeLiquida, "Quantidade após perda");

        return aplicarMovimentacao(insumo, usuario, quantidadeLiquida, motivo, observacao);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarSaida(
            Integer insumoId, BigDecimal quantidade, MotivoMovimentacaoEnum motivo,
            String observacao, Integer usuarioId)
    {
        validarPrecisaoQuantidade(quantidade, "Quantidade");

        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EstoqueInvalidoException("A quantidade deve ser maior que zero.");
        }

        if (motivo == null) {
            throw new EstoqueInvalidoException("Informe um motivo existente para a saída.");
        }

        if (motivo == MotivoMovimentacaoEnum.CONSUMO_PRODUTO
                || motivo == MotivoMovimentacaoEnum.SALDO_INICIAL
                || motivo == MotivoMovimentacaoEnum.VENDA
                || motivo == MotivoMovimentacaoEnum.CANCELAMENTO_VENDA) {
            throw new EstoqueInvalidoException(
                    "Este motivo é reservado aos fluxos de vendas ou consumo de produtos."
            );
        }

        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumo(insumoId);

        if (insumo.getQuantidadeAtual().compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException(
                    "O insumo possui saldo negativo preexistente e precisa ser regularizado por ajuste."
            );
        }

        BigDecimal quantidadeEfetivada = calcularQuantidadeEfetivada(quantidade, insumo.getQuantidadeAtual());

        if (quantidadeEfetivada.compareTo(BigDecimal.ZERO) == 0) {
            throw new EstoqueSemSaldoException();
        }

        MovimentacaoEstoqueResponseDto resposta = aplicarMovimentacao(
                insumo, usuario, quantidadeEfetivada.negate(), motivo, observacao
        );

        resposta.setQuantidadeSolicitada(quantidade);
        resposta.setQuantidadeEfetivada(quantidadeEfetivada);

        if (quantidade.compareTo(quantidadeEfetivada) > 0) {
            resposta.setAviso(
                    "A quantidade solicitada excede o disponível em estoque. O saldo ficou zerado; somente a quantidade disponível foi descontada."
            );
        }

        return resposta;
    }

    @Transactional
    public void registrarSaidaPorConsumo(ImportacaoLinhaConsumo consumo, Integer usuarioId) {
        if (consumo.getId() == null || consumo.getQuantidadeEfetivada() != null) {
            throw new EstoqueInvalidoException("Consumo não persistido ou já efetivado.");
        }

        validarPrecisaoQuantidade(consumo.getQuantidadePrevista(), "Consumo previsto");

        if (consumo.getQuantidadePrevista().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EstoqueInvalidoException("Consumo previsto deve ser positivo.");
        }

        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumo(consumo.getInsumo().getId());

        if (!Boolean.TRUE.equals(insumo.getAtivo())) {
            throw new EstoqueInvalidoException("Insumo desativado.");
        }

        if (insumo.getQuantidadeAtual().compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException(
                    "O insumo possui saldo negativo preexistente e precisa ser regularizado por ajuste."
            );
        }

        BigDecimal quantidadeEfetivada = calcularQuantidadeEfetivada(
                consumo.getQuantidadePrevista(), insumo.getQuantidadeAtual()
        );

        if (quantidadeEfetivada.compareTo(BigDecimal.ZERO) > 0) {
            aplicarMovimentacao(
                    insumo, usuario, quantidadeEfetivada.negate(),
                    MotivoMovimentacaoEnum.CONSUMO_PRODUTO, null, consumo
            );
        }

        consumo.setQuantidadeEfetivada(quantidadeEfetivada);

        if (quantidadeEfetivada.compareTo(consumo.getQuantidadePrevista()) < 0) {
            consumo.setAviso("Estoque insuficiente: saldo zerado; descontada somente a quantidade disponível.");
        }
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto ajustarSaldo(
            Integer insumoId, BigDecimal novoSaldo, String observacao, Integer usuarioId)
    {
        validarPrecisaoQuantidade(novoSaldo, "Novo saldo");

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException("O novo saldo não pode ser negativo.");
        }

        validarObservacao(observacao);
        Usuario usuario = buscarUsuario(usuarioId);
        Insumo insumo = buscarInsumo(insumoId);
        BigDecimal variacaoSaldo = novoSaldo.subtract(insumo.getQuantidadeAtual());

        if (variacaoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            throw new AjusteSemDiferencaException();
        }

        return aplicarMovimentacao(insumo, usuario, variacaoSaldo, MotivoMovimentacaoEnum.AJUSTE, observacao);
    }

    private MovimentacaoEstoqueResponseDto aplicarMovimentacao(
            Insumo insumo, Usuario usuario, BigDecimal variacaoSaldo,
            MotivoMovimentacaoEnum motivo, String observacao)
    {
        return aplicarMovimentacao(insumo, usuario, variacaoSaldo, motivo, observacao, null);
    }

    private MovimentacaoEstoqueResponseDto aplicarMovimentacao(
            Insumo insumo, Usuario usuario, BigDecimal variacaoSaldo,
            MotivoMovimentacaoEnum motivo, String observacao, ImportacaoLinhaConsumo consumo)
    {
        BigDecimal quantidadeMovimentacao = variacaoSaldo.abs();
        BigDecimal novoSaldo = insumo.getQuantidadeAtual().add(variacaoSaldo);

        validarPrecisaoQuantidade(quantidadeMovimentacao, "Quantidade da movimentação");
        validarPrecisaoQuantidade(novoSaldo, "Saldo resultante");

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueInvalidoException("O saldo resultante não pode ser negativo.");
        }

        if (quantidadeMovimentacao.compareTo(BigDecimal.ZERO) == 0) {
            throw new EstoqueInvalidoException("Uma movimentação deve ter quantidade maior que zero.");
        }

        MovimentacaoEstoque movimentacao = movimentacaoEstoqueService.registrar(
                insumo.getId(), usuario, variacaoSaldo, motivo, observacao, consumo
        );

        return MovimentacaoEstoqueMapper.toResponseDto(movimentacao);
    }

    private Insumo buscarInsumo(Integer insumoId) {
        if (insumoId == null) {
            throw new EstoqueInvalidoException("O insumo é obrigatório.");
        }

        Optional<Insumo> insumo = insumoRepository.findInsumoById(insumoId);

        if (insumo.isEmpty()) {
            throw new InsumoNotFoundException();
        }

        Insumo insumoEncontrado = insumo.get();

        if (!Boolean.TRUE.equals(insumoEncontrado.getAtivo())) {
            throw new InsumoDesativadoException();
        }

        return insumoEncontrado;
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

    static BigDecimal calcularQuantidadeLiquida(BigDecimal quantidadeBruta, Integer percentualPerda) {
        Integer percentualAproveitado = 100 - percentualPerda;
        BigDecimal quantidadeAproveitada = quantidadeBruta.multiply(BigDecimal.valueOf(percentualAproveitado));

        return quantidadeAproveitada.divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calcularQuantidadeEfetivada(BigDecimal quantidadeSolicitada, BigDecimal saldoDisponivel) {
        if (quantidadeSolicitada.compareTo(saldoDisponivel) > 0) {
            return saldoDisponivel;
        }

        return quantidadeSolicitada;
    }

    private void validarPrecisaoQuantidade(BigDecimal quantidade, String nomeCampo) {
        if (quantidade == null) {
            throw new EstoqueInvalidoException(nomeCampo + " é obrigatório.");
        }

        BigDecimal quantidadeSemZeros = quantidade.stripTrailingZeros();

        if (quantidadeSemZeros.scale() > 3 || quantidade.abs().compareTo(quantidadeMaximaEstoque) > 0) {
            throw new EstoqueInvalidoException(nomeCampo + " deve caber em DECIMAL(12,3), sem arredondamento.");
        }
    }

    private void validarObservacao(String observacao) {
        if (observacao != null && observacao.length() > 255) {
            throw new EstoqueInvalidoException("A observação deve ter no máximo 255 caracteres.");
        }
    }
}
