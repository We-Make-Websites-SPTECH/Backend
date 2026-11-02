package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.insumo.MovimentacaoResponseDto;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;

import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueMapper {

    public static MovimentacaoResponseDto toResponseDto(MovimentacaoEstoque movimentacaoEntity) {
        MovimentacaoResponseDto movimentacaoResponseDto = new MovimentacaoResponseDto();
        movimentacaoResponseDto.setId(movimentacaoEntity.getId());
        movimentacaoResponseDto.setTipo(movimentacaoEntity.getTipo());
        movimentacaoResponseDto.setMotivo(movimentacaoEntity.getMotivo());
        movimentacaoResponseDto.setQuantidade(movimentacaoEntity.getQuantidade());
        movimentacaoResponseDto.setDataHora(movimentacaoEntity.getDataHora());

        return movimentacaoResponseDto;
    }

    public static List<MovimentacaoResponseDto> toResponseDto(List<MovimentacaoEstoque> movimentacoesEntity) {
        List<MovimentacaoResponseDto> movimentacoesResponseDto = new ArrayList<>();
        for (MovimentacaoEstoque movimentacao : movimentacoesEntity) {
            movimentacoesResponseDto.add(toResponseDto(movimentacao));
        }
        return movimentacoesResponseDto;
    }
}
