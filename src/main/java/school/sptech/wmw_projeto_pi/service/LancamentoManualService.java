package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import school.sptech.wmw_projeto_pi.entity.*;
import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;
import school.sptech.wmw_projeto_pi.repository.*;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoDtos.*;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualDtos.*;

@Service
public class LancamentoManualService {
    private final ImportacaoRepository registros;
    private final ImportacaoLinhaRepository linhas;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;
    private final ImportacaoLinhaService worker;
    private final ImportacaoService importacoes;

    public LancamentoManualService(ImportacaoRepository registros,ImportacaoLinhaRepository linhas,
            ProdutoRepository produtos,UsuarioRepository usuarios,ImportacaoLinhaService worker,ImportacaoService importacoes) {
        this.registros=registros;this.linhas=linhas;this.produtos=produtos;
        this.usuarios=usuarios;this.worker=worker;this.importacoes=importacoes;
    }

    @Transactional(isolation=Isolation.REPEATABLE_READ)
    public Previa preparar(Pedido pedido,Integer usuarioId) {
        worker.validarUsuario(usuarioId);
        Importacao registro=new Importacao();
        registro.setOrigem(OrigemRegistroEnum.MANUAL);
        registro.setDataReferencia(pedido.dataReferencia());
        registro.setCriadoEm(LocalDateTime.now());
        registro.setUsuario(usuarios.findById(usuarioId).orElseThrow());
        registros.save(registro);
        int numero=0;
        for(Item item:pedido.itens()) {
            ImportacaoLinha linha=new ImportacaoLinha();
            linha.setImportacao(registro);linha.setNumeroLinha(++numero);
            linha.setIdOriginal(item.produtoId().toString());linha.setQuantidadeOriginal(item.quantidade().toString());
            worker.preparar(linha,item.produtoId(),nomeProduto(item.produtoId()),item.quantidade());
        }
        linhas.flush();
        return previa(registro.getId());
    }

    @Transactional(readOnly=true,isolation=Isolation.REPEATABLE_READ)
    public Previa consultar(Long id) { conferirManual(id);return previa(id); }

    @Transactional(isolation=Isolation.REPEATABLE_READ)
    public Linha corrigir(Long id,Long linhaId,CorrecaoManual pedido,Integer usuarioId) {
        conferirManual(id);
        return worker.corrigir(id,linhaId,new Correcao(pedido.produtoId(),nomeProduto(pedido.produtoId()),
                pedido.quantidade(),pedido.versao()),usuarioId);
    }

    public List<Resultado> confirmar(Long id,Confirmacao pedido,Integer usuarioId) {
        conferirManual(id);
        return importacoes.confirmar(id,pedido,usuarioId);
    }

    private String nomeProduto(Integer produtoId) {
        return produtos.findById(produtoId).map(Produto::getNome).orElse("Produto inexistente");
    }
    private void conferirManual(Long id) {
        Importacao registro=registros.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro inexistente."));
        if(registro.getOrigem()!=OrigemRegistroEnum.MANUAL)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro nao e um lancamento manual.");
    }
    private Previa previa(Long id) {
        List<Linha> detalhes=importacoes.linhas(id,0,100).getContent();
        Map<Integer,BigDecimal> totais=new TreeMap<>();
        for(Linha linha:detalhes) for(Consumo consumo:linha.consumo())
            totais.merge(consumo.insumoId(),consumo.previsto(),BigDecimal::add);
        return new Previa(importacoes.consultar(id),detalhes,totais.entrySet().stream()
                .map(e->new TotalConsumo(e.getKey(),e.getValue())).toList());
    }
}
