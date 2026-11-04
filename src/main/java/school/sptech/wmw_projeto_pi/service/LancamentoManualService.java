package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoPersonalizacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoInsumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoTotalResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualItemRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualPreviaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualRequestDto;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;
import school.sptech.wmw_projeto_pi.repository.ImportacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LancamentoManualService {
    private final ImportacaoRepository registrosRepository;
    private final ProcessamentoConsumoService processamentoConsumoService;
    private final RegistroConsumoService registroConsumoService;

    public LancamentoManualService(
            ImportacaoRepository registrosRepository,
            ProcessamentoConsumoService processamentoConsumoService,
            RegistroConsumoService registroConsumoService) {
        this.registrosRepository = registrosRepository;
        this.processamentoConsumoService = processamentoConsumoService;
        this.registroConsumoService = registroConsumoService;
    }

    @Transactional
    public LancamentoManualPreviaResponseDto preparar(LancamentoManualRequestDto lancamentoRequest, Integer usuarioId) {
        Usuario usuario = processamentoConsumoService.buscarUsuario(usuarioId);

        Importacao registro = new Importacao();
        registro.setOrigem(OrigemRegistroEnum.MANUAL);
        registro.setDataReferencia(lancamentoRequest.getDataReferencia());
        registro.setCreatedAt(LocalDateTime.now());
        registro.setUsuario(usuario);
        registrosRepository.save(registro);

        Integer numeroLinha = 0;

        for (LancamentoManualItemRequestDto item : lancamentoRequest.getItens()) {
            numeroLinha++;
            Integer quantidade = item.getQuantidade().intValueExact();

            ImportacaoLinha linha = new ImportacaoLinha();
            linha.setImportacao(registro);
            linha.setNumeroLinha(numeroLinha);

            processamentoConsumoService.prepararLinhaManual(
                    linha, item.getProdutoId(), quantidade
            );
        }

        registroConsumoService.validarExistenciaDeLinhasValidas(registro.getId());

        return montarPrevia(registro.getId());
    }

    @Transactional
    public LancamentoManualPreviaResponseDto consultar(Long id) {
        validarRegistroManual(id);

        return montarPrevia(id);
    }

    public ImportacaoLinhaResponseDto personalizar(
            Long id, Long linhaId, ConsumoPersonalizacaoRequestDto personalizacaoRequest, Integer usuarioId) {
        validarRegistroManual(id);

        return processamentoConsumoService.personalizar(id, linhaId, personalizacaoRequest, usuarioId);
    }

    public List<ImportacaoResultadoResponseDto> confirmar(
            Long id, ImportacaoConfirmacaoRequestDto confirmacaoRequest, Integer usuarioId) {
        validarRegistroManual(id);

        return registroConsumoService.confirmar(id, confirmacaoRequest, usuarioId);
    }

    private void validarRegistroManual(Long id) {
        Optional<Importacao> registroOptional = registrosRepository.findById(id);

        if (registroOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro inexistente.");
        }

        Importacao registro = registroOptional.get();

        if (registro.getOrigem() != OrigemRegistroEnum.MANUAL) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não é manual.");
        }
    }

    private LancamentoManualPreviaResponseDto montarPrevia(Long id) {
        List<ImportacaoLinhaResponseDto> detalhes = registroConsumoService.linhas(id);
        List<ConsumoTotalResponseDto> consumoAgregado = new ArrayList<>();

        for (ImportacaoLinhaResponseDto linha : detalhes) {
            for (ConsumoInsumoResponseDto consumo : linha.getConsumo()) {
                adicionarConsumoAoTotal(consumoAgregado, consumo);
            }
        }

        ImportacaoResumoResponseDto resumo = registroConsumoService.consultar(id);

        return new LancamentoManualPreviaResponseDto(resumo, detalhes, consumoAgregado);
    }

    private void adicionarConsumoAoTotal(List<ConsumoTotalResponseDto> consumoAgregado, ConsumoInsumoResponseDto consumo) {
        for (Integer posicao = 0; posicao < consumoAgregado.size(); posicao++) {
            ConsumoTotalResponseDto total = consumoAgregado.get(posicao);

            if (total.getInsumoId().equals(consumo.getInsumoId())) {
                BigDecimal quantidade = total.getQuantidadePrevista().add(consumo.getPrevisto());
                total.setQuantidadePrevista(quantidade);
                return;
            }

            if (total.getInsumoId() > consumo.getInsumoId()) {
                ConsumoTotalResponseDto novoTotal = new ConsumoTotalResponseDto(consumo.getInsumoId(), consumo.getPrevisto());
                consumoAgregado.add(posicao, novoTotal);
                return;
            }
        }

        ConsumoTotalResponseDto novoTotal = new ConsumoTotalResponseDto(consumo.getInsumoId(), consumo.getPrevisto());
        consumoAgregado.add(novoTotal);
    }
}
