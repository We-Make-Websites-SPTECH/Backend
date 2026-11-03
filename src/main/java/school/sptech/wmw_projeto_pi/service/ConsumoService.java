package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConsumoService {
    private final ProdutoInsumoRepository receitas;
    private final InsumoRepository insumos;

    public ConsumoService(
            ProdutoInsumoRepository receitas,
            InsumoRepository insumos) {
        this.receitas = receitas;
        this.insumos = insumos;
    }

    public static class Componente {
        private final Insumo insumo;
        private final BigDecimal quantidade;

        public Componente(Insumo insumo, BigDecimal quantidade) {
            this.insumo = insumo;
            this.quantidade = quantidade;
        }

        public Insumo getInsumo() {
            return insumo;
        }

        public BigDecimal getQuantidade() {
            return quantidade;
        }
    }

    public List<Componente> calcularConsumo(Produto produto, Integer quantidade) {
        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new ImportacaoInvalidaException("Produto desativado.");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new ImportacaoInvalidaException("Quantidade deve ser inteira e positiva.");
        }

        List<ProdutoInsumo> receita = receitas.findByProdutoId(produto.getId());

        if (receita.isEmpty()) {
            throw new ImportacaoInvalidaException("Produto sem receita.");
        }

        List<Componente> agregado = new ArrayList<>();

        for (ProdutoInsumo item : receita) {
            Insumo insumo = item.getInsumo();

            if (!Boolean.TRUE.equals(insumo.getAtivo())) {
                throw new ImportacaoInvalidaException("Receita contém insumo desativado.");
            }

            BigDecimal valor = item.getQuantidade().multiply(BigDecimal.valueOf(quantidade));
            int posicao = encontrarPosicao(agregado, insumo.getId());

            if (posicao < agregado.size()
                    && agregado.get(posicao).getInsumo().getId().equals(insumo.getId())) {
                Componente anterior = agregado.get(posicao);
                valor = valor.add(anterior.getQuantidade());
            }

            if (valor.compareTo(BigDecimal.ZERO) <= 0
                    || valor.stripTrailingZeros().scale() > 3
                    || valor.compareTo(new BigDecimal("999999999.999")) > 0) {
                throw new ImportacaoInvalidaException(
                        "Consumo deve ser positivo e caber em DECIMAL(12,3), sem arredondamento."
                );
            }

            salvarComponente(agregado, posicao, insumo, valor);
        }

        return agregado;
    }

    public List<Componente> calcularPersonalizado(
            Produto produto, Integer quantidade, List<ConsumoVariacaoRequestDto> variacoes) {
        long alteradas = 0;

        for (ConsumoVariacaoRequestDto variacao : variacoes) {
            try {
                alteradas = Math.addExact(alteradas, variacao.getUnidades().intValueExact());
            } catch (ArithmeticException e) {
                throw new ImportacaoInvalidaException("Unidades devem ser inteiras positivas.");
            }

            if (variacao.getUnidades().signum() <= 0 || alteradas > quantidade) {
                throw new ImportacaoInvalidaException("Variações excedem a quantidade de produtos.");
            }
        }

        List<Componente> base = calcularConsumo(produto, 1);
        List<Componente> total = new ArrayList<>();

        for (Componente componente : base) {
            adicionar(
                    total, componente.getInsumo(),
                    componente.getQuantidade().multiply(BigDecimal.valueOf(quantidade - alteradas))
            );
        }

        for (ConsumoVariacaoRequestDto variacao : variacoes) {
            Set<Integer> usados = new HashSet<>();

            for (ConsumoIngredienteRequestDto ingrediente : variacao.getIngredientes()) {
                if (!usados.add(ingrediente.getInsumoId())) {
                    throw new ImportacaoInvalidaException("Ingrediente repetido na mesma variação.");
                }

                Insumo insumo = insumos.findById(ingrediente.getInsumoId())
                        .orElseThrow(() -> new ImportacaoInvalidaException("Insumo inexistente."));

                if (!Boolean.TRUE.equals(insumo.getAtivo())) {
                    throw new ImportacaoInvalidaException("Insumo desativado.");
                }

                if (ingrediente.getQuantidade() == null || ingrediente.getQuantidade().signum() <= 0) {
                    throw new ImportacaoInvalidaException("Quantidade do ingrediente deve ser positiva.");
                }

                adicionar(total, insumo, ingrediente.getQuantidade().multiply(variacao.getUnidades()));
            }
        }

        return total;
    }

    private void adicionar(List<Componente> total, Insumo insumo, BigDecimal valor) {
        if (valor.signum() == 0) {
            return;
        }

        int posicao = encontrarPosicao(total, insumo.getId());

        if (posicao < total.size()
                && total.get(posicao).getInsumo().getId().equals(insumo.getId())) {
            Componente anterior = total.get(posicao);
            valor = valor.add(anterior.getQuantidade());
        }

        if (valor.signum() < 0
                || valor.stripTrailingZeros().scale() > 3
                || valor.compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new ImportacaoInvalidaException("Consumo deve caber em DECIMAL(12,3), sem arredondamento.");
        }

        salvarComponente(total, posicao, insumo, valor);
    }

    private int encontrarPosicao(List<Componente> componentes, Integer insumoId) {
        int posicao = 0;

        while (posicao < componentes.size()
                && componentes.get(posicao).getInsumo().getId() < insumoId) {
            posicao++;
        }

        return posicao;
    }

    private void salvarComponente(
            List<Componente> componentes, int posicao, Insumo insumo, BigDecimal quantidade) {
        Componente componente = new Componente(insumo, quantidade);

        if (posicao < componentes.size()
                && componentes.get(posicao).getInsumo().getId().equals(insumo.getId())) {
            componentes.set(posicao, componente);
        } else {
            componentes.add(posicao, componente);
        }
    }
}
