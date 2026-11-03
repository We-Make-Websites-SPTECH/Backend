package school.sptech.wmw_projeto_pi.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;
import school.sptech.wmw_projeto_pi.entity.*;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;
import school.sptech.wmw_projeto_pi.repository.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;
import school.sptech.wmw_projeto_pi.mapper.ImportacaoMapper;

@Service
public class ImportacaoService {
    private final ImportacaoRepository lotes;
    private final ImportacaoLinhaRepository linhas;
    private final ImportacaoLinhaConsumoRepository consumos;
    private final UsuarioRepository usuarios;
    private final ImportacaoTxtParser parser;
    private final ImportacaoLinhaService worker;
    public ImportacaoService(ImportacaoRepository lotes,ImportacaoLinhaRepository linhas,ImportacaoLinhaConsumoRepository consumos,
                             UsuarioRepository usuarios,ImportacaoTxtParser parser,ImportacaoLinhaService worker) {
        this.lotes=lotes;this.linhas=linhas;this.consumos=consumos;this.usuarios=usuarios;this.parser=parser;this.worker=worker;
    }
    @Transactional(isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public Resumo receber(String nome,byte[] bytes,LocalDate data,Integer usuarioId) {
        worker.validarUsuario(usuarioId);
        var arquivo=parser.analisar(bytes);
        Importacao lote=new Importacao(); lote.setNomeArquivo(nome==null?"importacao.txt":nome.substring(0,Math.min(nome.length(),255)));
        lote.setConteudoOriginal(arquivo.texto()); lote.setDataReferencia(data); lote.setCriadoEm(LocalDateTime.now());
        lote.setUsuario(usuarios.findById(usuarioId).orElseThrow()); lotes.save(lote);
        for(var original:arquivo.linhas()) {
            var linha=new ImportacaoLinha(); linha.setImportacao(lote); linha.setNumeroLinha(original.numero());
            linha.setTextoOriginal(original.original()); linha.setIdOriginal(original.id()); linha.setNomeAtual(original.nome());
            linha.setQuantidadeOriginal(original.quantidade());
            if(original.erro()!=null) {
                linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);linha.setErroCodigo("FORMATO_INVALIDO");linha.setErroMensagem(original.erro());linhas.save(linha);
            } else worker.preparar(linha,ImportacaoTxtParser.inteiro(original.id()),original.nome(),ImportacaoTxtParser.inteiro(original.quantidade()));
        }
        linhas.flush(); return resumo(lote);
    }
    @Transactional(readOnly=true)
    public Page<Resumo> listar(int pagina,int tamanho) { return lotes.findAll(PageRequest.of(pagina,tamanho,Sort.by("id").descending())).map(this::resumo); }
    @Transactional(readOnly=true)
    public Resumo consultar(Long id) { return resumo(buscar(id)); }
    @Transactional(readOnly=true)
    public Page<Linha> linhas(Long id,int pagina,int tamanho) {
        buscar(id); return linhas.findByImportacaoId(id,PageRequest.of(pagina,tamanho,Sort.by("numeroLinha"))).map(l->ImportacaoMapper.linha(l,consumos.findByLinhaIdOrderByInsumoIdAsc(l.getId())));
    }
    public List<Resultado> confirmar(Long id,Confirmacao pedido,Integer usuarioId) {
        buscar(id);worker.validarUsuario(usuarioId);
        List<Resultado> resultados=new ArrayList<>(); Set<Long> ids=new HashSet<>();
        for(Selecao selecao:pedido.linhas()) {
            if(!ids.add(selecao.linhaId())) continue;
            try { resultados.add(worker.confirmarLinha(id,selecao,usuarioId)); }
            catch(RuntimeException e) {
                String codigo=e instanceof ResponseStatusException r?"HTTP_"+r.getStatusCode().value():"FALHA_PROCESSAMENTO";
                String mensagem=e instanceof ResponseStatusException r?r.getReason():"Não foi possível confirmar a linha; a transação foi desfeita.";
                if(!(e instanceof ResponseStatusException)) {
                    try { worker.registrarFalha(id,selecao.linhaId(),codigo); } catch(RuntimeException ignorada) {  }
                }
                resultados.add(new Resultado(selecao.linhaId(),false,codigo,mensagem,null));
            }
        }
        return resultados;
    }
    private Importacao buscar(Long id) { return lotes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Importação inexistente.")); }
    private Resumo resumo(Importacao lote) {
        return new Resumo(lote.getId(),lote.getOrigem(),lote.getNomeArquivo(),lote.getDataReferencia(),lote.getCriadoEm(),
                linhas.countByImportacaoIdAndStatus(lote.getId(),StatusImportacaoLinhaEnum.PENDENTE),
                linhas.countByImportacaoIdAndStatus(lote.getId(),StatusImportacaoLinhaEnum.INVALIDA),
                linhas.countByImportacaoIdAndStatus(lote.getId(),StatusImportacaoLinhaEnum.PROCESSADA));
    }
}
