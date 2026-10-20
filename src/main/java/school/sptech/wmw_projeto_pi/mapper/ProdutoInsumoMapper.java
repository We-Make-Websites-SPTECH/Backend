package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.produto.ProdutoUsuarioResponseDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ProdutoInsumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.ProdutoResumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.InsumoResumoResponseDto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;

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
}