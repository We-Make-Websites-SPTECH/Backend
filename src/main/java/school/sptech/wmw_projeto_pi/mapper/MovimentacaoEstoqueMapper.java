package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.estoque.MovimentacaoEstoqueResponseDto;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.dto.insumo.MovimentacaoResponseDto;

import java.util.ArrayList;
import java.util.List;

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

    public static MovimentacaoResponseDto toHistoricoResponseDto(MovimentacaoEstoque movimentacao) {
        MovimentacaoResponseDto dto = new MovimentacaoResponseDto();
        dto.setId(movimentacao.getId());
        dto.setTipo(movimentacao.getTipo().name());
        dto.setMotivo(movimentacao.getMotivo().name());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setDataHora(movimentacao.getDataHora());
        return dto;
    }

    public static List<MovimentacaoResponseDto> toHistoricoResponseDto(List<MovimentacaoEstoque> movimentacoes) {
        List<MovimentacaoResponseDto> historico = new ArrayList<>();
        for (MovimentacaoEstoque movimentacao : movimentacoes) {
            historico.add(toHistoricoResponseDto(movimentacao));
        }
        return historico;
    }
}
