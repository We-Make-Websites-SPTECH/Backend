package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoInsumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;

import java.util.ArrayList;
import java.util.List;

public final class ImportacaoMapper {

    public static ImportacaoLinhaResponseDto toResponseDto(ImportacaoLinha linha,
                                                           List<ImportacaoLinhaConsumo> componentes)
    {
        List<ConsumoInsumoResponseDto> consumos = new ArrayList<>();

        for (ImportacaoLinhaConsumo componente : componentes) {
            consumos.add(toConsumoResponseDto(componente));
        }

        return new ImportacaoLinhaResponseDto(
                linha.getId(),
                linha.getNumeroLinha(),
                linha.getProduto() == null ? null : linha.getProduto().getId(),
                linha.getNomeAtual(),
                linha.getQuantidadeAtual(),
                linha.getStatus(),
                linha.getVersao(),
                linha.getErroCodigo(),
                linha.getErroMensagem(),
                linha.getProcessadoEm(),
                consumos
        );
    }

    private static ConsumoInsumoResponseDto toConsumoResponseDto(ImportacaoLinhaConsumo componente) {
        return new ConsumoInsumoResponseDto(
                componente.getId(),
                componente.getInsumo().getId(),
                componente.getQuantidadePrevista(),
                componente.getQuantidadeEfetivada(),
                componente.getAviso()
        );
    }
}
