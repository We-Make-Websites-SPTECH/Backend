package school.sptech.wmw_projeto_pi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImportacaoTxtParser {
    @Value("${importacao.max-bytes:1048576}")
    private int maxBytes;

    @Value("${importacao.max-linhas:10000}")
    private int maxLinhas;

    public static class Linha {
        private final int numero;
        private final String id;
        private final String nome;
        private final String quantidade;
        private final String erro;

        public Linha(int numero, String id, String nome, String quantidade, String erro) {
            this.numero = numero;
            this.id = id;
            this.nome = nome;
            this.quantidade = quantidade;
            this.erro = erro;
        }

        public int getNumero() {
            return numero;
        }

        public String getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getQuantidade() {
            return quantidade;
        }

        public String getErro() {
            return erro;
        }
    }

    public static class Arquivo {
        private final List<Linha> linhas;

        public Arquivo(List<Linha> linhas) {
            this.linhas = linhas;
        }

        public List<Linha> getLinhas() {
            return linhas;
        }
    }

    public Arquivo analisar(byte[] bytes) {
        if (bytes.length > maxBytes) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "Arquivo excede o limite.");
        }

        String texto;
        try {
            texto = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo deve ser UTF-8 válido.");
        }

        String leitura = texto;

        if (texto.startsWith("\uFEFF")) {
            leitura = texto.substring(1);
        }
        String[] linhas = leitura.split("\r?\n", -1);
        List<Linha> resultado = new ArrayList<>();
        boolean primeira = true;

        for (int i = 0; i < linhas.length; i++) {
            String original = linhas[i];

            if (original.isBlank()) {
                continue;
            }

            String[] colunas = original.split(";", -1);

            for (int j = 0; j < colunas.length; j++) {
                colunas[j] = colunas[j].trim();
            }

            if (primeira && colunas.length == 3
                    && colunas[0].equalsIgnoreCase("ID")
                    && colunas[1].equalsIgnoreCase("NOME")
                    && colunas[2].equalsIgnoreCase("QUANTIDADE")) {
                primeira = false;
                continue;
            }

            primeira = false;
            String id = colunas[0];
            String nome = "";
            String quantidade = "";
            String erro = null;

            if (colunas.length > 1) {
                nome = colunas[1];
            }

            if (colunas.length > 2) {
                quantidade = colunas[2];
            }

            if (colunas.length != 3) {
                erro = "Use exatamente ID;NOME;QUANTIDADE.";
            }

            if (erro == null && (inteiro(id) == null || inteiro(quantidade) == null || nome.isBlank())) {
                erro = "ID e quantidade devem ser inteiros positivos; nome é obrigatório.";
            }

            resultado.add(new Linha(i + 1, id, nome, quantidade, erro));

            if (resultado.size() > maxLinhas) {
                throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "Arquivo excede o limite de linhas.");
            }
        }

        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo sem linhas de produto.");
        }

        return new Arquivo(resultado);
    }

    public static Integer inteiro(String texto) {
        if (texto == null || !texto.matches("[0-9]+")) {
            return null;
        }

        try {
            int valor = Integer.parseInt(texto);

            if (valor <= 0) {
                return null;
            }

            return valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
