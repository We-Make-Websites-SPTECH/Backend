package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ProdutoInsumoRequestDto {
    @NotNull(message = "O ID do produto é obrigatório.")
    @Positive
    private Integer produtoId;

    @NotEmpty
    private List<@NotNull @Valid ProdutoInsumoItemRequestDto> ingredientes;

    public ProdutoInsumoRequestDto() {
    }

    public ProdutoInsumoRequestDto(Integer produtoId, List<ProdutoInsumoItemRequestDto> ingredientes) {
        this.produtoId = produtoId;
        this.ingredientes = ingredientes;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public List<ProdutoInsumoItemRequestDto> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<ProdutoInsumoItemRequestDto> ingredientes) {
        this.ingredientes = ingredientes;
    }
}
