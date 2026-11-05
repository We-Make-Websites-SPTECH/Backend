package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.dto.interno.LinhaTxtDto;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;
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
import java.util.Optional;

@Service
public class RegistroConsumoService {
    private final ImportacaoRepository lotesRepository;
    private final ImportacaoLinhaRepository linhasRepository;
    private final ImportacaoLinhaConsumoRepository consumosRepository;
    private final ImportacaoTxtParser txtParser;
    private final ProcessamentoConsumoService processamentoConsumoService;

    public RegistroConsumoService(
            ImportacaoRepository lotesRepository,
            ImportacaoLinhaRepository linhasRepository,
            ImportacaoLinhaConsumoRepository consumosRepository,
            ImportacaoTxtParser txtParser,
            ProcessamentoConsumoService processamentoConsumoService) {
        this.lotesRepository = lotesRepository;
        this.linhasRepository = linhasRepository;
        this.consumosRepository = consumosRepository;
        this.txtParser = txtParser;
        this.processamentoConsumoService = processamentoConsumoService;
    }

    @Transactional
    public ImportacaoResumoResponseDto receber(String nomeArquivo, byte[] bytes, LocalDate dataReferencia, Integer usuarioId) {
        Usuario usuario = processamentoConsumoService.buscarUsuario(usuarioId);
        List<LinhaTxtDto> linhasRecebidas = txtParser.analisar(bytes);

        if (nomeArquivo == null) {
            nomeArquivo = "importacao.txt";
        }

        Importacao lote = new Importacao();
        lote.setNomeArquivo(ProcessamentoConsumoService.limitarTexto(nomeArquivo, 255));
        lote.setDataReferencia(dataReferencia);
        lote.setCreatedAt(LocalDateTime.now());
        lote.setUsuario(usuario);
        lotesRepository.save(lote);

        for (LinhaTxtDto linhaRecebida : linhasRecebidas) {
            Integer produtoId = ImportacaoTxtParser.converterParaInteiro(linhaRecebida.getId());
            Integer quantidade = ImportacaoTxtParser.converterParaInteiro(linhaRecebida.getQuantidade());
            String nomeProduto = linhaRecebida.getNome();

            if (nomeProduto.isBlank()) {
                nomeProduto = "Produto ID " + linhaRecebida.getId();
            }

            ImportacaoLinha linha = new ImportacaoLinha();
            linha.setImportacao(lote);
            linha.setNumeroLinha(linhaRecebida.getNumero());
            linha.setNomeAtual(ProcessamentoConsumoService.limitarTexto(nomeProduto, 255));
            linha.setQuantidadeAtual(quantidade);

            if (linhaRecebida.getErro() != null) {
                linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
                linha.setErroCodigo("FORMATO_INVALIDO");
                linha.setErroMensagem(linhaRecebida.getErro());
                linhasRepository.save(linha);
            } else {
                processamentoConsumoService.prepararLinhaTxt(
                        linha, produtoId, linhaRecebida.getNome(), quantidade
                );
            }
        }

        validarExistenciaDeLinhasValidas(lote.getId());

        return montarResumo(lote);
    }

    public void validarExistenciaDeLinhasValidas(Long id) {
        if (linhasRepository.countByImportacaoIdAndStatus(id, StatusImportacaoLinhaEnum.PENDENTE) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Registro sem linhas válidas. Corrija os dados e envie novamente."
            );
        }
    }

    @Transactional
    public List<ImportacaoResumoResponseDto> listar() {
        List<ImportacaoResumoResponseDto> registros = new ArrayList<>();

        for (Importacao lote : lotesRepository.findByStatusOrderByIdDesc(StatusImportacaoEnum.CONCLUIDO)) {
            ImportacaoResumoResponseDto resumo = montarResumo(lote);
            registros.add(resumo);
        }

        return registros;
    }

    @Transactional
    public ImportacaoResumoResponseDto consultar(Long id) {
        Importacao registro = buscarRegistro(id);

        return montarResumo(registro);
    }

    @Transactional
    public List<ImportacaoLinhaResponseDto> linhas(Long id) {
        buscarRegistro(id);
        List<ImportacaoLinhaResponseDto> detalhes = new ArrayList<>();

        for (ImportacaoLinha linha : linhasRepository.findByImportacaoIdOrderByNumeroLinhaAsc(id)) {
            List<ImportacaoLinhaConsumo> consumos = consumosRepository.findByLinhaIdOrderByInsumoIdAsc(linha.getId());
            ImportacaoLinhaResponseDto detalhe = ImportacaoMapper.toResponseDto(linha, consumos);
            detalhes.add(detalhe);
        }

        return detalhes;
    }

    public List<ImportacaoResultadoResponseDto> confirmar(
            Long id, ImportacaoConfirmacaoRequestDto confirmacaoRequest, Integer usuarioId) {
        List<Long> idsDasLinhas = processamentoConsumoService.iniciarConfirmacao(id, confirmacaoRequest, usuarioId);
        List<ImportacaoResultadoResponseDto> resultados = new ArrayList<>();

        for (Long linhaId : idsDasLinhas) {
            try {
                ImportacaoResultadoResponseDto resultado = processamentoConsumoService.confirmarLinha(id, linhaId, usuarioId);
                resultados.add(resultado);
            } catch (RuntimeException e) {
                ImportacaoResultadoResponseDto resultado = processamentoConsumoService.registrarFalha(id, linhaId);
                resultados.add(resultado);
            }
        }

        processamentoConsumoService.concluirRegistro(id);

        return resultados;
    }

    private Importacao buscarRegistro(Long id) {
        Optional<Importacao> registroOptional = lotesRepository.findById(id);

        if (registroOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro inexistente.");
        }

        return registroOptional.get();
    }

    private ImportacaoResumoResponseDto montarResumo(Importacao lote) {
        Long quantidadePendente = linhasRepository.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.PENDENTE);
        Long quantidadeInvalida = linhasRepository.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.INVALIDA);
        Long quantidadeProcessada = linhasRepository.countByImportacaoIdAndStatus(lote.getId(), StatusImportacaoLinhaEnum.PROCESSADA);

        return new ImportacaoResumoResponseDto(
                lote.getId(),
                lote.getOrigem(),
                lote.getNomeArquivo(),
                lote.getDataReferencia(),
                lote.getCreatedAt(),
                lote.getStatus(),
                quantidadePendente,
                quantidadeInvalida,
                quantidadeProcessada
        );
    }
}
