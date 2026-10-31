package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.estoque.MovimentacaoEstoqueResponseDto;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;

public class MovimentacaoEstoqueMapper {
    public static MovimentacaoEstoqueResponseDto toResponseDto(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueResponseDto dto = new MovimentacaoEstoqueResponseDto();
        dto.setId(movimentacao.getId());
        dto.setInsumoId(movimentacao.getInsumo().getId());
        dto.setUsuarioId(movimentacao.getUsuario().getId());
        dto.setTipo(movimentacao.getTipo());
        dto.setMotivo(movimentacao.getMotivo());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setSaldoAtual(movimentacao.getInsumo().getQuantidadeAtual());
        dto.setObservacao(movimentacao.getObservacao());
        dto.setDataHora(movimentacao.getDataHora());
        return dto;
    }
}
