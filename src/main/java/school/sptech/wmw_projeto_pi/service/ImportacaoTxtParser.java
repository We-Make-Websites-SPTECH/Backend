package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.interno.LinhaTxtDto;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportacaoTxtParser {
    private final Integer maxBytes = 1_048_576;

    private final Integer maxLinhas = 250;

    public List<LinhaTxtDto> analisar(byte[] bytes) {
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

        String[] linhas = texto.split("\r?\n", -1);
        List<LinhaTxtDto> resultado = new ArrayList<>();
        Boolean primeira = true;

        for (Integer i = 0; i < linhas.length; i++) {
            String original = linhas[i];

            if (original.isBlank()) {
                continue;
            }

            String[] colunas = original.split(";", -1);

            for (Integer j = 0; j < colunas.length; j++) {
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

            if (erro == null && (converterParaInteiro(id) == null || converterParaInteiro(quantidade) == null || nome.isBlank())) {
                erro = "ID e quantidade devem ser inteiros positivos; nome é obrigatório.";
            }

            resultado.add(new LinhaTxtDto(i + 1, id, nome, quantidade, erro));

            if (resultado.size() > maxLinhas) {
                throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "Arquivo excede o limite de linhas.");
            }
        }

        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo sem linhas de produto.");
        }

        return resultado;
    }

    public static Integer converterParaInteiro(String texto) {
        if (texto == null || !texto.matches("[0-9]+")) {
            return null;
        }

        try {
            Integer valor = Integer.parseInt(texto);

            if (valor <= 0) {
                return null;
            }

            return valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
