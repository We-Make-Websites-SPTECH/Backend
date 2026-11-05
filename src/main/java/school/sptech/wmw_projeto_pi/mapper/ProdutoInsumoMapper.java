package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.produtoInsumo.*;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.entity.Usuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProdutoInsumoMapper {

    public static ProdutoInsumo toEntity(Produto produto, Insumo insumo, BigDecimal quantidade, Usuario usuario) {
        ProdutoInsumo entity = new ProdutoInsumo();

        entity.setProduto(produto);
        entity.setInsumo(insumo);
        entity.setQuantidade(quantidade);
        entity.setCreatedBy(usuario);
        entity.setUpdatedBy(usuario);

        return entity;
    }

    public static ReceitaResponseDto toReceitaResponseDto(
            List<ProdutoInsumo> produtoInsumoList
    ) {
        ReceitaResponseDto dto = new ReceitaResponseDto();

        if (produtoInsumoList == null || produtoInsumoList.isEmpty()) {
            dto.setIngredientes(new ArrayList<>());
            return dto;
        }

        Produto produto = produtoInsumoList.get(0).getProduto();

        if (produto != null) {
            ProdutoResumoResponseDto produtoDto = new ProdutoResumoResponseDto();

            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setTamanho(produto.getTamanho());

            dto.setProduto(produtoDto);
        }

        List<ReceitaIngredienteResponseDto> ingredientes = new ArrayList<>();

        for (ProdutoInsumo pi : produtoInsumoList) {
            ReceitaIngredienteResponseDto ingredienteDto =
                    new ReceitaIngredienteResponseDto();

            ingredienteDto.setId(pi.getId());
            ingredienteDto.setQuantidade(pi.getQuantidade());

            if (pi.getInsumo() != null) {
                InsumoResumoResponseDto insumoDto = new InsumoResumoResponseDto();

                insumoDto.setId(pi.getInsumo().getId());
                insumoDto.setNome(pi.getInsumo().getNome());
                insumoDto.setUnidadeMedida(pi.getInsumo().getUnidadeMedida());

                ingredienteDto.setInsumo(insumoDto);
            }

            ingredientes.add(ingredienteDto);
        }

        dto.setIngredientes(ingredientes);

        return dto;
    }
}