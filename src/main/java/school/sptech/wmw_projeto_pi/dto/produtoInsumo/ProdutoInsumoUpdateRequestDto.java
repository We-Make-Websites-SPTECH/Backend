package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ProdutoInsumoUpdateRequestDto {

    @NotEmpty(message = "A receita deve possuir pelo menos um ingrediente.")
    @Valid
    private List<ProdutoInsumoItemRequestDto> ingredientes;

    public ProdutoInsumoUpdateRequestDto() {
    }

    public ProdutoInsumoUpdateRequestDto(List<ProdutoInsumoItemRequestDto> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<ProdutoInsumoItemRequestDto> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<ProdutoInsumoItemRequestDto> ingredientes) {
        this.ingredientes = ingredientes;
    }
}
