package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;
import school.sptech.wmw_projeto_pi.mapper.ImportacaoMapper;
import school.sptech.wmw_projeto_pi.repository.ImportacaoLinhaConsumoRepository;
import school.sptech.wmw_projeto_pi.repository.ImportacaoLinhaRepository;
import school.sptech.wmw_projeto_pi.repository.ImportacaoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroConsumoService {
    private final ImportacaoRepository lotes;
    private final ImportacaoLinhaRepository linhas;
    private final ImportacaoLinhaConsumoRepository consumos;
    private final ImportacaoTxtParser parser;
    private final ProcessamentoConsumoService processamento;

    public RegistroConsumoService(
            ImportacaoRepository lotes,
            ImportacaoLinhaRepository linhas,
            ImportacaoLinhaConsumoRepository consumos,
            ImportacaoTxtParser parser,
            ProcessamentoConsumoService processamento) {
        this.lotes = lotes;
        this.linhas = linhas;
        this.consumos = consumos;
        this.parser = parser;
        this.processamento = processamento;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ImportacaoResumoResponseDto receber(String nome, byte[] bytes, LocalDate data, Integer usuarioId) {
        Usuario usuario = processamento.buscarUsuario(usuarioId);
        ImportacaoTxtParser.Arquivo arquivo = parser.analisar(bytes);

        if (nome == null) {
            nome = "importacao.txt";
        }

        Importacao lote = new Importacao();
        lote.setNomeArquivo(ProcessamentoConsumoService.limitar(nome, 255));
        lote.setDataReferencia(data);
        lote.setCreatedAt(LocalDateTime.now());
        lote.setUsuario(usuario);
        lotes.save(lote);

        for (ImportacaoTxtParser.Linha recebida : arquivo.getLinhas()) {
            ImportacaoLinha linha = new ImportacaoLinha();
            linha.setImportacao(lote);
            linha.setNumeroLinha(recebida.getNumero());
            String nomeProduto = recebida.getNome();

            if (nomeProduto.isBlank()) {
                nomeProduto = "Produto ID " + recebida.getId();
            }

            linha.setNomeAtual(ProcessamentoConsumoService.limitar(nomeProduto, 255));
            linha.setQuantidadeAtual(ImportacaoTxtParser.inteiro(recebida.getQuantidade()));

            if (recebida.getErro() != null) {
                linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
                linha.setErroCodigo("FORMATO_INVALIDO");
                linha.setErroMensagem(recebida.getErro());
                linhas.save(linha);
            } else {
                processamento.preparar(
                        linha, ImportacaoTxtParser.inteiro(recebida.getId()),
                        recebida.getNome(), linha.getQuantidadeAtual()
                );
            }
        }

        linhas.flush();
        exigirValidas(lote.getId());

        return resumo(lote);
    }

    public void exigirValidas(Long id) {
        if (linhas.countByImportacaoIdAndStatus(id, StatusImportacaoLinhaEnum.PENDENTE) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Registro sem linhas válidas. Corrija os dados e envie novamente."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ImportacaoResumoResponseDto> listar() {
        List<ImportacaoResumoResponseDto> registros = new ArrayList<>();

        for (Importacao lote : lotes.findByStatusOrderByIdDesc(StatusImportacaoEnum.CONCLUIDO)) {
            registros.add(resumo(lote));
        }

        return registros;
    }

    @Transactional(readOnly = true)
    public ImportacaoResumoResponseDto consultar(Long id) {
        return resumo(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ImportacaoLinhaResponseDto> linhas(Long id) {
        buscar(id);
        List<ImportacaoLinhaResponseDto> detalhes = new ArrayList<>();

        for (ImportacaoLinha linha : linhas.findByImportacaoIdOrderByNumeroLinhaAsc(id)) {
            detalhes.add(ImportacaoMapper.toResponseDto(
                    linha, consumos.findByLinhaIdOrderByInsumoIdAsc(linha.getId())
            ));
        }

        return detalhes;
    }

    public List<ImportacaoResultadoResponseDto> confirmar(
            Long id, ImportacaoConfirmacaoRequestDto pedido, Integer usuarioId) {
        List<Long> ids = processamento.iniciar(id, pedido, usuarioId);
        List<ImportacaoResultadoResponseDto> resultados = new ArrayList<>();

        for (Long linhaId : ids) {
            try {
                resultados.add(processamento.confirmarLinha(id, linhaId, usuarioId));
            } catch (RuntimeException e) {
                resultados.add(processamento.registrarFalha(id, linhaId));
            }
        }

        processamento.encerrar(id);

        return resultados;
    }

    private Importacao buscar(Long id) {
        return lotes.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Registro inexistente."
        ));
    }

    private ImportacaoResumoResponseDto resumo(Importacao lote) {
        return new ImportacaoResumoResponseDto(
                lote.getId(),
                lote.getOrigem(),
                lote.getNomeArquivo(),
                lote.getDataReferencia(),
                lote.getCreatedAt(),
                lote.getStatus(),
                linhas.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.PENDENTE),
                linhas.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.INVALIDA),
                linhas.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.PROCESSADA)
        );
    }
}
