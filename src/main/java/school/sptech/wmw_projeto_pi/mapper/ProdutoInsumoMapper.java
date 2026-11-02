package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.produto.ProdutoUsuarioResponseDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.*;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.entity.Usuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProdutoInsumoMapper {

    public static ProdutoInsumoResponseDto toResponseDto(ProdutoInsumo entity) {
        ProdutoInsumoResponseDto dto = new ProdutoInsumoResponseDto();

        dto.setId(entity.getId());
        dto.setQuantidade(entity.getQuantidade());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getProduto() != null) {
            ProdutoResumoResponseDto produtoDto = new ProdutoResumoResponseDto();

            produtoDto.setId(entity.getProduto().getId());
            produtoDto.setNome(entity.getProduto().getNome());
            produtoDto.setTamanho(entity.getProduto().getTamanho());

            dto.setProduto(produtoDto);
        }

        if (entity.getInsumo() != null) {
            InsumoResumoResponseDto insumoDto = new InsumoResumoResponseDto();

            insumoDto.setId(entity.getInsumo().getId());
            insumoDto.setNome(entity.getInsumo().getNome());
            insumoDto.setUnidadeMedida(entity.getInsumo().getUnidadeMedida());

            dto.setInsumo(insumoDto);
        }

        if (entity.getCreatedBy() != null) {
            ProdutoUsuarioResponseDto createdByDto = new ProdutoUsuarioResponseDto();

            createdByDto.setId(entity.getCreatedBy().getId());
            createdByDto.setNome(entity.getCreatedBy().getNome());

            dto.setCreatedBy(createdByDto);
        }

        if (entity.getUpdatedBy() != null) {
            ProdutoUsuarioResponseDto updatedByDto = new ProdutoUsuarioResponseDto();

            updatedByDto.setId(entity.getUpdatedBy().getId());
            updatedByDto.setNome(entity.getUpdatedBy().getNome());

            dto.setUpdatedBy(updatedByDto);
        }

        return dto;
    }

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