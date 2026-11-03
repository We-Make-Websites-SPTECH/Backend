package school.sptech.wmw_projeto_pi.service;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

@Component
public class ImportacaoTxtParser {
    @Value("${importacao.max-bytes:1048576}") private int maxBytes;
    @Value("${importacao.max-linhas:10000}") private int maxLinhas;
    public record Linha(int numero,String original,String id,String nome,String quantidade,String erro) {}
    public record Arquivo(String texto,List<Linha> linhas) {}
    public Arquivo analisar(byte[] bytes) {
        if(bytes.length>maxBytes) throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE,"Arquivo excede o limite.");
        String texto;
        try { texto=StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString(); }
        catch(CharacterCodingException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O arquivo deve ser UTF-8 válido."); }
        String leitura=texto.startsWith("\uFEFF")?texto.substring(1):texto;
        String[] linhas=leitura.split("\r?\n",-1);
        List<Linha> resultado=new ArrayList<>(); boolean primeira=true;
        for(int i=0;i<linhas.length;i++) {
            String original=linhas[i]; if(original.isBlank()) continue;
            String[] c=original.split(";",-1);
            for(int j=0;j<c.length;j++) c[j]=c[j].trim();
            if(primeira && c.length==3 && c[0].equalsIgnoreCase("ID") && c[1].equalsIgnoreCase("NOME") && c[2].equalsIgnoreCase("QUANTIDADE")) { primeira=false; continue; }
            primeira=false;
            String id=c.length>0?c[0]:"", nome=c.length>1?c[1]:"", qtd=c.length>2?c[2]:"";
            String erro=c.length!=3?"Use exatamente ID;NOME;QUANTIDADE.":null;
            if(erro==null && (inteiro(id)==null || inteiro(qtd)==null || nome.isBlank())) erro="ID e quantidade devem ser inteiros positivos; nome é obrigatório.";
            resultado.add(new Linha(i+1,original,id,nome,qtd,erro));
            if(resultado.size()>maxLinhas) throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE,"Arquivo excede o limite de linhas.");
        }
        if(resultado.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Arquivo sem linhas de produto.");
        return new Arquivo(texto,resultado);
    }
    public static Integer inteiro(String texto) {
        if(texto==null || !texto.matches("[0-9]+")) return null;
        try { int valor=Integer.parseInt(texto); return valor>0?valor:null; }
        catch(NumberFormatException e) { return null; }
    }
}
