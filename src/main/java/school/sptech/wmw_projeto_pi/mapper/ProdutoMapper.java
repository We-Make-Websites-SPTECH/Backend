package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.produto.ProdutoRequestDtoCreate;
import school.sptech.wmw_projeto_pi.dto.produto.ProdutoRequestDtoUpdate;
import school.sptech.wmw_projeto_pi.dto.produto.ProdutoResponseDtoGetAll;
import school.sptech.wmw_projeto_pi.entity.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoMapper {

    public static ProdutoResponseDtoGetAll toResponseDto(Produto produtoEntity) {
        ProdutoResponseDtoGetAll produtoResponseDto = new ProdutoResponseDtoGetAll();

        produtoResponseDto.setId(produtoEntity.getId());
        produtoResponseDto.setNome(produtoEntity.getNome());
        produtoResponseDto.setAtivo(produtoEntity.getAtivo());
        produtoResponseDto.setTipo(produtoEntity.getTipo());
        produtoResponseDto.setTamanho(produtoEntity.getTamanho());
        produtoResponseDto.setCreatedAt(produtoEntity.getCreatedAt());
        produtoResponseDto.setCreatedBy(
                UsuarioMapper.toUsuarioResponseDto(produtoEntity.getCreatedBy())
        );
        produtoResponseDto.setUpdatedAt(produtoEntity.getUpdatedAt());
        produtoResponseDto.setUpdatedBy(
                UsuarioMapper.toUsuarioResponseDto(produtoEntity.getUpdatedBy())
        );

        return produtoResponseDto;
    }

    public static List<ProdutoResponseDtoGetAll> toResponseDto(
            List<Produto> produtosEntity
    ) {
        List<ProdutoResponseDtoGetAll> produtosResponseDtoGetAll = new ArrayList<>();

        for (Produto p : produtosEntity) {
            produtosResponseDtoGetAll.add(toResponseDto(p));
        }

        return produtosResponseDtoGetAll;
    }

    public static Produto toEntityCreate(
            ProdutoRequestDtoCreate produtoRequestDtoCreate
    ) {
        if (produtoRequestDtoCreate == null) {
            return null;
        }

        Produto produto = new Produto();

        produto.setNome(produtoRequestDtoCreate.getNome());
        produto.setTipo(produtoRequestDtoCreate.getTipo());
        produto.setTamanho(produtoRequestDtoCreate.getTamanho());

        return produto;
    }

    public static Produto toEntityUpdate(
            ProdutoRequestDtoUpdate produtoRequestDtoUpdate
    ) {
        if (produtoRequestDtoUpdate == null) {
            return null;
        }

        Produto produto = new Produto();

        produto.setNome(produtoRequestDtoUpdate.getNome());
        produto.setAtivo(produtoRequestDtoUpdate.getAtivo());
        produto.setTipo(produtoRequestDtoUpdate.getTipo());
        produto.setTamanho(produtoRequestDtoUpdate.getTamanho());

        return produto;
    }
}