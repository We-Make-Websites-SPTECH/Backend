package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import java.util.List;

public class ReceitaResponseDto {

    private ProdutoResumoResponseDto produto;
    private List<ReceitaIngredienteResponseDto> ingredientes;

    public ProdutoResumoResponseDto getProduto() {
        return produto;
    }

    public void setProduto(ProdutoResumoResponseDto produto) {
        this.produto = produto;
    }

    public List<ReceitaIngredienteResponseDto> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<ReceitaIngredienteResponseDto> ingredientes) {
        this.ingredientes = ingredientes;
    }
}