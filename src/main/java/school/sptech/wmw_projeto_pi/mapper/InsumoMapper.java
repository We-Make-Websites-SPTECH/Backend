package school.sptech.wmw_projeto_pi.mapper;

import school.sptech.wmw_projeto_pi.dto.insumo.InsumoRequestDtoCreate;
import school.sptech.wmw_projeto_pi.dto.insumo.InsumoRequestDtoUpdate;
import school.sptech.wmw_projeto_pi.dto.insumo.InsumoResponseDto;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.enums.UnidadeMedidaEnum;
import school.sptech.wmw_projeto_pi.exception.UnidadeMedidaInvalidaException;

import java.util.ArrayList;
import java.util.List;

public class InsumoMapper {

    public static InsumoResponseDto toResponseDto(Insumo insumoEntity) {
        InsumoResponseDto insumoResponseDto = new InsumoResponseDto();
        insumoResponseDto.setId(insumoEntity.getId());
        insumoResponseDto.setNome(insumoEntity.getNome());
        insumoResponseDto.setUnidadeMedida(toUnidadeResponse(insumoEntity.getUnidadeMedida()));
        insumoResponseDto.setSaldo(insumoEntity.getQuantidadeAtual());
        insumoResponseDto.setAtivo(insumoEntity.getAtivo());

        return insumoResponseDto;
    }

    public static List<InsumoResponseDto> toResponseDto(List<Insumo> insumosEntity) {
        List<InsumoResponseDto> insumosResponseDto = new ArrayList<>();
        for (Insumo insumo : insumosEntity) {
            insumosResponseDto.add(toResponseDto(insumo));
        }
        return insumosResponseDto;
    }

    public static Insumo toEntityCreate(InsumoRequestDtoCreate insumoRequestDto) {
        if (insumoRequestDto == null) {
            return null;
        }

        Insumo insumo = new Insumo();
        insumo.setNome(insumoRequestDto.getNome());
        insumo.setUnidadeMedida(toUnidadeEntity(insumoRequestDto.getUnidadeMedida()));
        insumo.setQuantidadeAtual(insumoRequestDto.getSaldoInicial());

        return insumo;
    }

    public static Insumo toEntityUpdate(InsumoRequestDtoUpdate insumoRequestDto) {
        if (insumoRequestDto == null) {
            return null;
        }

        Insumo insumo = new Insumo();
        insumo.setNome(insumoRequestDto.getNome());
        insumo.setUnidadeMedida(toUnidadeEntity(insumoRequestDto.getUnidadeMedida()));
        insumo.setQuantidadeAtual(insumoRequestDto.getSaldo());

        return insumo;
    }

    private static UnidadeMedidaEnum toUnidadeEntity(String unidadeMedida) {
        if ("g".equals(unidadeMedida)) return UnidadeMedidaEnum.GRAMA;
        if ("ml".equals(unidadeMedida)) return UnidadeMedidaEnum.ML;
        if ("un".equals(unidadeMedida)) return UnidadeMedidaEnum.UNIDADE;

        throw new UnidadeMedidaInvalidaException();
    }

    private static String toUnidadeResponse(UnidadeMedidaEnum unidadeMedida) {
        if (unidadeMedida == UnidadeMedidaEnum.GRAMA) return "g";
        if (unidadeMedida == UnidadeMedidaEnum.ML) return "ml";
        if (unidadeMedida == UnidadeMedidaEnum.UNIDADE) return "un";

        throw new UnidadeMedidaInvalidaException();
    }
}
