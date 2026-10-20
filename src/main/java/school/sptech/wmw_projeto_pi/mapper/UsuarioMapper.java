package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.UsuarioResponseDto;
import school.sptech.wmw_projeto_pi.entity.Usuario;

public class UsuarioMapper {

    public static UsuarioResponseDto toUsuarioResponseDto(Usuario usuarioEntity) {
        if (usuarioEntity == null) {
            return null;
        }

        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(usuarioEntity.getId());
        usuarioResponseDto.setNome(usuarioEntity.getNome());

        return usuarioResponseDto;
    }




}
