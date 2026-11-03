package school.sptech.wmw_projeto_pi.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import school.sptech.wmw_projeto_pi.entity.*;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;
import school.sptech.wmw_projeto_pi.repository.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;
import school.sptech.wmw_projeto_pi.mapper.ImportacaoMapper;
import school.sptech.wmw_projeto_pi.exception.ImportacaoInvalidaException;

@Service
public class ImportacaoLinhaService {
    private final ImportacaoLinhaRepository linhas;
    private final ImportacaoLinhaConsumoRepository consumos;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;
    private final ConsumoService motor;
    public ImportacaoLinhaService(ImportacaoLinhaRepository linhas,ImportacaoLinhaConsumoRepository consumos,
                                 ProdutoRepository produtos,UsuarioRepository usuarios,ConsumoService motor) {
        this.linhas=linhas; this.consumos=consumos; this.produtos=produtos; this.usuarios=usuarios; this.motor=motor;
    }
    public void preparar(ImportacaoLinha linha,Integer produtoId,String nome,Integer quantidade) {
        linha.setNomeAtual(nome); linha.setQuantidadeAtual(quantidade); linha.setProduto(null);
        try {
            if(produtoId==null || quantidade==null || quantidade<=0 || nome==null || nome.isBlank())
                throw new ImportacaoInvalidaException("ID, nome e quantidade inteira positiva são obrigatórios.");
            Produto produto=produtos.findById(produtoId).orElseThrow(()->new ImportacaoInvalidaException("Produto inexistente."));
            linha.setProduto(produto);
            if(!produto.getNome().trim().equalsIgnoreCase(nome.trim())) throw new ImportacaoInvalidaException("Nome não corresponde ao ID do produto.");
            var componentes=motor.calcularConsumo(produtoId,quantidade);
            linha.setStatus(StatusImportacaoLinhaEnum.PENDENTE); linha.setErroCodigo(null); linha.setErroMensagem(null);
            linhas.saveAndFlush(linha);
            for(var componente:componentes) {
                ImportacaoLinhaConsumo consumo=new ImportacaoLinhaConsumo();
                consumo.setLinha(linha); consumo.setInsumo(componente.insumo()); consumo.setQuantidadePrevista(componente.quantidade());
                consumos.save(consumo);
            }
        } catch(ImportacaoInvalidaException e) {
            linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA); linha.setErroCodigo("LINHA_INVALIDA"); linha.setErroMensagem(e.getMessage()); linhas.save(linha);
        }
    }
    private ImportacaoLinha bloquear(Long loteId,Long linhaId) {
        var linha=linhas.buscarComBloqueio(linhaId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Linha inexistente."));
        if(!linha.getImportacao().getId().equals(loteId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Linha não pertence ao lote.");
        return linha;
    }
    private void conferirVersao(ImportacaoLinha linha,Long versao) {
        if(!Objects.equals(linha.getVersao(),versao)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Prévia alterada; consulte a linha novamente.");
    }
    @Transactional(isolation=org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
    public Linha corrigir(Long loteId,Long linhaId,Correcao dados,Integer usuarioId) {
        validarUsuario(usuarioId);
        var linha=bloquear(loteId,linhaId);
        if(linha.getStatus()==StatusImportacaoLinhaEnum.PROCESSADA) throw new ResponseStatusException(HttpStatus.CONFLICT,"Linha já processada.");
        conferirVersao(linha,dados.versao());
        consumos.deleteAll(consumos.findByLinhaIdOrderByInsumoIdAsc(linhaId)); consumos.flush();
        linha.marcarCorrecao();
        preparar(linha,dados.produtoId(),dados.nome().trim(),dados.quantidade());
        linhas.flush();
        return ImportacaoMapper.linha(linha,consumos.findByLinhaIdOrderByInsumoIdAsc(linhaId));
    }
    @Transactional
    public Resultado confirmarLinha(Long loteId,Selecao selecao,Integer usuarioId) {
        validarUsuario(usuarioId);
        var linha=bloquear(loteId,selecao.linhaId());
        if(linha.getStatus()==StatusImportacaoLinhaEnum.PROCESSADA)
            return new Resultado(linha.getId(),true,null,null,detalhe(linha));
        conferirVersao(linha,selecao.versao());
        if(linha.getStatus()!=StatusImportacaoLinhaEnum.PENDENTE) throw new ResponseStatusException(HttpStatus.CONFLICT,"Corrija a linha inválida antes de confirmar.");
        if(!Boolean.TRUE.equals(linha.getProduto().getAtivo())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Produto desativado.");
        var componentes=consumos.findByLinhaIdOrderByInsumoIdAsc(linha.getId());
        if(componentes.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Linha sem prévia de consumo.");
        motor.efetivarBaixa(componentes,usuarioId);
        linha.setStatus(StatusImportacaoLinhaEnum.PROCESSADA); linha.setProcessadoEm(LocalDateTime.now());
        linha.setErroCodigo(null); linha.setErroMensagem(null); linhas.saveAndFlush(linha);
        return new Resultado(linha.getId(),false,null,null,detalhe(linha));
    }
    @Transactional
    public void registrarFalha(Long loteId,Long linhaId,String codigo) {
        var linha=bloquear(loteId,linhaId);
        if(linha.getStatus()==StatusImportacaoLinhaEnum.PENDENTE) {
            linha.setErroCodigo(codigo); linha.setErroMensagem("Falha na confirmação. Consulte a situação e tente novamente.");
        }
    }
    private Linha detalhe(ImportacaoLinha linha) { return ImportacaoMapper.linha(linha,consumos.findByLinhaIdOrderByInsumoIdAsc(linha.getId())); }
    public void validarUsuario(Integer id) {
        if(id==null || !usuarios.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário inexistente.");
    }
}
