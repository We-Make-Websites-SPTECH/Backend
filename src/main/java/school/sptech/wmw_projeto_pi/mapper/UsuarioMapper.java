package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.produto.ProdutoUsuarioResponseDto;
import school.sptech.wmw_projeto_pi.entity.Usuario;

public class UsuarioMapper {

    public static ProdutoUsuarioResponseDto toUsuarioResponseDto(Usuario usuarioEntity) {
        if (usuarioEntity == null) {
            return null;
        }

        ProdutoUsuarioResponseDto usuarioResponseDto = new ProdutoUsuarioResponseDto();
        usuarioResponseDto.setId(usuarioEntity.getId());
        usuarioResponseDto.setNome(usuarioEntity.getNome());

        return usuarioResponseDto;
    }




}
