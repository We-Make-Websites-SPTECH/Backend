package school.sptech.wmw_projeto_pi.mapper;
import school.sptech.wmw_projeto_pi.entity.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;
import java.util.List;
public class ImportacaoMapper {
    public static Linha linha(ImportacaoLinha l,List<ImportacaoLinhaConsumo> componentes) {
        return new Linha(l.getId(),l.getNumeroLinha(),l.getTextoOriginal(),l.getIdOriginal(),l.getQuantidadeOriginal(),
                l.getProduto()==null?null:l.getProduto().getId(),l.getNomeAtual(),l.getQuantidadeAtual(),l.getStatus(),
                l.getVersao(),l.getErroCodigo(),l.getErroMensagem(),l.getProcessadoEm(),componentes.stream()
                .map(c->new Consumo(c.getId(),c.getInsumo().getId(),c.getQuantidadePrevista(),c.getQuantidadeEfetivada(),c.getAviso())).toList());
    }
}
