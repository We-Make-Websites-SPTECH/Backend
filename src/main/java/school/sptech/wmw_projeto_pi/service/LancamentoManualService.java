package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoPersonalizacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoInsumoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoTotalResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualItemRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualPreviaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.LancamentoManualRequestDto;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.OrigemRegistroEnum;
import school.sptech.wmw_projeto_pi.repository.ImportacaoLinhaRepository;
import school.sptech.wmw_projeto_pi.repository.ImportacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LancamentoManualService {
    private final ImportacaoRepository registros;
    private final ImportacaoLinhaRepository linhas;
    private final ProcessamentoConsumoService processamento;
    private final RegistroConsumoService registrosConsumo;

    public LancamentoManualService(
            ImportacaoRepository registros,
            ImportacaoLinhaRepository linhas,
            ProcessamentoConsumoService processamento,
            RegistroConsumoService registrosConsumo) {
        this.registros = registros;
        this.linhas = linhas;
        this.processamento = processamento;
        this.registrosConsumo = registrosConsumo;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LancamentoManualPreviaResponseDto preparar(LancamentoManualRequestDto pedido, Integer usuarioId) {
        Usuario usuario = processamento.buscarUsuario(usuarioId);

        Importacao registro = new Importacao();
        registro.setOrigem(OrigemRegistroEnum.MANUAL);
        registro.setDataReferencia(pedido.getDataReferencia());
        registro.setCreatedAt(LocalDateTime.now());
        registro.setUsuario(usuario);
        registros.save(registro);

        int numero = 0;

        for (LancamentoManualItemRequestDto item : pedido.getItens()) {
            ImportacaoLinha linha = new ImportacaoLinha();
            linha.setImportacao(registro);
            linha.setNumeroLinha(++numero);

            processamento.prepararManual(linha, item.getProdutoId(), item.getQuantidade().intValueExact());
        }

        linhas.flush();
        registrosConsumo.exigirValidas(registro.getId());

        return previa(registro.getId());
    }

    @Transactional(readOnly = true)
    public LancamentoManualPreviaResponseDto consultar(Long id) {
        conferirManual(id);

        return previa(id);
    }

    public ImportacaoLinhaResponseDto personalizar(
            Long id, Long linhaId, ConsumoPersonalizacaoRequestDto pedido, Integer usuarioId) {
        conferirManual(id);

        return processamento.personalizar(id, linhaId, pedido, usuarioId);
    }

    public List<ImportacaoResultadoResponseDto> confirmar(
            Long id, ImportacaoConfirmacaoRequestDto pedido, Integer usuarioId) {
        conferirManual(id);

        return registrosConsumo.confirmar(id, pedido, usuarioId);
    }

    private void conferirManual(Long id) {
        Importacao registro = registros.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Registro inexistente."
        ));

        if (registro.getOrigem() != OrigemRegistroEnum.MANUAL) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não é manual.");
        }
    }

    private LancamentoManualPreviaResponseDto previa(Long id) {
        List<ImportacaoLinhaResponseDto> detalhes = registrosConsumo.linhas(id);
        List<ConsumoTotalResponseDto> consumoAgregado = new ArrayList<>();

        for (ImportacaoLinhaResponseDto linha : detalhes) {
            for (ConsumoInsumoResponseDto consumo : linha.getConsumo()) {
                int posicao = 0;

                while (posicao < consumoAgregado.size()
                        && consumoAgregado.get(posicao).getInsumoId() < consumo.getInsumoId()) {
                    posicao++;
                }

                if (posicao < consumoAgregado.size()
                        && consumoAgregado.get(posicao).getInsumoId().equals(consumo.getInsumoId())) {
                    ConsumoTotalResponseDto total = consumoAgregado.get(posicao);
                    BigDecimal quantidade = total.getQuantidadePrevista().add(consumo.getPrevisto());
                    total.setQuantidadePrevista(quantidade);
                } else {
                    consumoAgregado.add(posicao, new ConsumoTotalResponseDto(
                            consumo.getInsumoId(), consumo.getPrevisto()
                    ));
                }
            }
        }

        return new LancamentoManualPreviaResponseDto(registrosConsumo.consultar(id), detalhes, consumoAgregado);
    }
}
