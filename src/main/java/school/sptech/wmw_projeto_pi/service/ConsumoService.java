package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.dto.interno.ComponenteConsumoDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoIngredienteRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoVariacaoRequestDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.exception.ImportacaoInvalidaException;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.ProdutoInsumoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsumoService {
    private final ProdutoInsumoRepository receitasRepository;
    private final InsumoRepository insumosRepository;
    private final BigDecimal quantidadeMaximaConsumo = new BigDecimal("999999999.999");

    public ConsumoService(ProdutoInsumoRepository receitasRepository, InsumoRepository insumosRepository) {
        this.receitasRepository = receitasRepository;
        this.insumosRepository = insumosRepository;
    }

    public List<ComponenteConsumoDto> calcularConsumo(Produto produto, Integer quantidade) {
        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new ImportacaoInvalidaException("Produto desativado.");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new ImportacaoInvalidaException("Quantidade deve ser inteira e positiva.");
        }

        List<ProdutoInsumo> receita = receitasRepository.findByProdutoId(produto.getId());

        if (receita.isEmpty()) {
            throw new ImportacaoInvalidaException("Produto sem receita.");
        }

        List<ComponenteConsumoDto> consumoTotal = new ArrayList<>();

        for (ProdutoInsumo ingredienteReceita : receita) {
            Insumo insumo = ingredienteReceita.getInsumo();

            if (!Boolean.TRUE.equals(insumo.getAtivo())) {
                throw new ImportacaoInvalidaException("Receita contém insumo desativado.");
            }

            BigDecimal quantidadeConsumo = ingredienteReceita.getQuantidade().multiply(BigDecimal.valueOf(quantidade));

            adicionarConsumo(consumoTotal, insumo, quantidadeConsumo, false,
                    "Consumo deve ser positivo e caber em DECIMAL(12,3), sem arredondamento."
            );
        }

        return consumoTotal;
    }

    public List<ComponenteConsumoDto> calcularPersonalizado(
            Produto produto, Integer quantidade,
            List<ConsumoVariacaoRequestDto> variacoes)
    {
        Long quantidadeUnidadesPersonalizadas = 0L;

        for (ConsumoVariacaoRequestDto variacao : variacoes) {
            Integer unidadesPersonalizadas;

            try {
                unidadesPersonalizadas = variacao.getUnidades().intValueExact();
            } catch (ArithmeticException e) {
                throw new ImportacaoInvalidaException("Unidades devem ser inteiras positivas.");
            }

            quantidadeUnidadesPersonalizadas = quantidadeUnidadesPersonalizadas + unidadesPersonalizadas;

            if (unidadesPersonalizadas <= 0 || quantidadeUnidadesPersonalizadas > quantidade) {
                throw new ImportacaoInvalidaException("Variações excedem a quantidade de produtos.");
            }
        }

        List<ComponenteConsumoDto> consumoPadrao = calcularConsumo(produto, 1);
        List<ComponenteConsumoDto> consumoTotal = new ArrayList<>();
        Long quantidadeUnidadesPadrao = quantidade - quantidadeUnidadesPersonalizadas;

        for (ComponenteConsumoDto componente : consumoPadrao) {
            BigDecimal quantidadeConsumo = componente.getQuantidade()
                    .multiply(BigDecimal.valueOf(quantidadeUnidadesPadrao));

            adicionarConsumo(consumoTotal, componente.getInsumo(), quantidadeConsumo, true,
                    "Consumo deve caber em DECIMAL(12,3), sem arredondamento."
            );
        }

        for (ConsumoVariacaoRequestDto variacao : variacoes) {
            List<Integer> insumosAdicionados = new ArrayList<>();

            for (ConsumoIngredienteRequestDto ingrediente : variacao.getIngredientes()) {
                if (insumosAdicionados.contains(ingrediente.getInsumoId())) {
                    throw new ImportacaoInvalidaException("Ingrediente repetido na mesma variação.");
                }

                insumosAdicionados.add(ingrediente.getInsumoId());

                Optional<Insumo> insumoOptional = insumosRepository.findById(ingrediente.getInsumoId());

                if (insumoOptional.isEmpty()) {
                    throw new ImportacaoInvalidaException("Insumo inexistente.");
                }

                Insumo insumo = insumoOptional.get();

                if (!Boolean.TRUE.equals(insumo.getAtivo())) {
                    throw new ImportacaoInvalidaException("Insumo desativado.");
                }

                if (ingrediente.getQuantidade() == null || ingrediente.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ImportacaoInvalidaException("Quantidade do ingrediente deve ser positiva.");
                }

                BigDecimal quantidadeConsumo = ingrediente.getQuantidade().multiply(variacao.getUnidades());

                adicionarConsumo(consumoTotal, insumo, quantidadeConsumo, true,
                        "Consumo deve caber em DECIMAL(12,3), sem arredondamento."
                );
            }
        }

        return consumoTotal;
    }

    private void adicionarConsumo(
            List<ComponenteConsumoDto> consumoTotal, Insumo insumo, BigDecimal quantidadeConsumo,
            Boolean ignorarQuantidadeZero, String mensagemErro)
    {
        if (ignorarQuantidadeZero && quantidadeConsumo.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        Integer posicao = 0;

        while (posicao < consumoTotal.size()
                && consumoTotal.get(posicao).getInsumo().getId() < insumo.getId()) {
            posicao++;
        }

        ComponenteConsumoDto componenteExistente = null;

        if (posicao < consumoTotal.size() && consumoTotal.get(posicao).getInsumo().getId().equals(insumo.getId()))
        {
            componenteExistente = consumoTotal.get(posicao);
            quantidadeConsumo = quantidadeConsumo.add(componenteExistente.getQuantidade());
        }

        if (!ignorarQuantidadeZero && quantidadeConsumo.compareTo(BigDecimal.ZERO) == 0) {
            throw new ImportacaoInvalidaException(mensagemErro);
        }

        validarQuantidadeConsumo(quantidadeConsumo, mensagemErro);

        if (componenteExistente == null) {
            consumoTotal.add(posicao, new ComponenteConsumoDto(insumo, quantidadeConsumo));
        } else {
            componenteExistente.setInsumo(insumo);
            componenteExistente.setQuantidade(quantidadeConsumo);
        }
    }

    private void validarQuantidadeConsumo(BigDecimal quantidadeConsumo, String mensagemErro) {
        if (quantidadeConsumo.compareTo(BigDecimal.ZERO) < 0
                || quantidadeConsumo.stripTrailingZeros().scale() > 3
                || quantidadeConsumo.compareTo(quantidadeMaximaConsumo) > 0) {
            throw new ImportacaoInvalidaException(mensagemErro);
        }
    }
}
