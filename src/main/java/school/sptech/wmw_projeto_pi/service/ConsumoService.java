package school.sptech.wmw_projeto_pi.service;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import school.sptech.wmw_projeto_pi.entity.*;
import school.sptech.wmw_projeto_pi.repository.*;
import school.sptech.wmw_projeto_pi.exception.ImportacaoInvalidaException;

@Service
public class ConsumoService {
    private final ProdutoRepository produtos;
    private final ProdutoInsumoRepository receitas;
    private final EstoqueService estoque;
    public ConsumoService(ProdutoRepository produtos,ProdutoInsumoRepository receitas,EstoqueService estoque) {
        this.produtos=produtos; this.receitas=receitas; this.estoque=estoque;
    }
    public record Componente(Insumo insumo,BigDecimal quantidade) {}
    public List<Componente> calcularConsumo(Integer produtoId,Integer quantidade) {
        Produto produto=produtos.findById(produtoId).orElseThrow(()->new ImportacaoInvalidaException("Produto inexistente."));
        if(!Boolean.TRUE.equals(produto.getAtivo())) throw new ImportacaoInvalidaException("Produto desativado.");
        if(quantidade==null || quantidade<=0) throw new ImportacaoInvalidaException("Quantidade deve ser inteira e positiva.");
        List<ProdutoInsumo> receita=receitas.findByProdutoId(produtoId);
        if(receita.isEmpty()) throw new ImportacaoInvalidaException("Produto sem receita.");
        Map<Integer,Componente> agregado=new TreeMap<>();
        for(ProdutoInsumo item:receita) {
            Insumo insumo=item.getInsumo();
            if(!Boolean.TRUE.equals(insumo.getAtivo())) throw new ImportacaoInvalidaException("Receita contém insumo desativado.");
            BigDecimal valor=item.getQuantidade().multiply(BigDecimal.valueOf(quantidade));
            Componente anterior=agregado.get(insumo.getId());
            if(anterior!=null) valor=valor.add(anterior.quantidade());
            if(valor.compareTo(BigDecimal.ZERO)<=0 || valor.stripTrailingZeros().scale()>3 || valor.compareTo(new BigDecimal("999999999.999"))>0)
                throw new ImportacaoInvalidaException("Consumo deve ser positivo e caber em DECIMAL(12,3), sem arredondamento.");
            agregado.put(insumo.getId(),new Componente(insumo,valor));
        }
        return new ArrayList<>(agregado.values());
    }
    public void efetivarBaixa(List<ImportacaoLinhaConsumo> componentes,Integer usuarioId) {
        componentes.sort(Comparator.comparing(c->c.getInsumo().getId()));
        for(ImportacaoLinhaConsumo componente:componentes) estoque.registrarSaidaPorConsumo(componente,usuarioId);
    }
}
