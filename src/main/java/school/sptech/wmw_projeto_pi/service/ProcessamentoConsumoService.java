package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.importacao.ConsumoPersonalizacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoConfirmacaoRequestDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoLinhaResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoResultadoResponseDto;
import school.sptech.wmw_projeto_pi.dto.importacao.ImportacaoSelecaoRequestDto;
import school.sptech.wmw_projeto_pi.entity.Importacao;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinha;
import school.sptech.wmw_projeto_pi.entity.ImportacaoLinhaConsumo;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoEnum;
import school.sptech.wmw_projeto_pi.enums.StatusImportacaoLinhaEnum;
import school.sptech.wmw_projeto_pi.exception.ImportacaoInvalidaException;
import school.sptech.wmw_projeto_pi.mapper.ImportacaoMapper;
import school.sptech.wmw_projeto_pi.repository.ImportacaoLinhaConsumoRepository;
import school.sptech.wmw_projeto_pi.repository.ImportacaoLinhaRepository;
import school.sptech.wmw_projeto_pi.repository.ImportacaoRepository;
import school.sptech.wmw_projeto_pi.repository.ProdutoRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class ProcessamentoConsumoService {
    private final ImportacaoRepository lotes;
    private final ImportacaoLinhaRepository linhas;
    private final ImportacaoLinhaConsumoRepository consumos;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;
    private final ConsumoService calculoConsumo;
    private final EstoqueService estoque;

    public ProcessamentoConsumoService(
            ImportacaoRepository lotes,
            ImportacaoLinhaRepository linhas,
            ImportacaoLinhaConsumoRepository consumos,
            ProdutoRepository produtos,
            UsuarioRepository usuarios,
            ConsumoService calculoConsumo,
            EstoqueService estoque) {
        this.lotes = lotes;
        this.linhas = linhas;
        this.consumos = consumos;
        this.produtos = produtos;
        this.usuarios = usuarios;
        this.calculoConsumo = calculoConsumo;
        this.estoque = estoque;
    }

    public void preparar(ImportacaoLinha linha, Integer produtoId, String nome, Integer quantidade) {
        preparar(linha, produtoId, nome, quantidade, false);
    }

    public void prepararManual(ImportacaoLinha linha, Integer produtoId, Integer quantidade) {
        preparar(linha, produtoId, "Produto ID " + produtoId, quantidade, true);
    }

    private void preparar(
            ImportacaoLinha linha, Integer produtoId, String nome, Integer quantidade, boolean manual) {
        linha.setNomeAtual(limitar(nome, 255));
        linha.setQuantidadeAtual(quantidade);

        try {
            if (produtoId == null || quantidade == null || quantidade <= 0 || nome == null || nome.isBlank()) {
                throw new ImportacaoInvalidaException("ID, nome e quantidade inteira positiva são obrigatórios.");
            }

            Produto produto = produtos.findById(produtoId)
                    .orElseThrow(() -> new ImportacaoInvalidaException("Produto inexistente."));
            linha.setProduto(produto);

            if (!manual && !produto.getNome().trim().equalsIgnoreCase(nome.trim())) {
                throw new ImportacaoInvalidaException("Nome não corresponde ao ID do produto.");
            }

            if (manual) {
                linha.setNomeAtual(limitar(produto.getNome(), 255));
            }

            List<ConsumoService.Componente> calculado = calculoConsumo.calcularConsumo(produto, quantidade);
            linha.setNomeAtual(limitar(produto.getNome(), 255));
            linha.setStatus(StatusImportacaoLinhaEnum.PENDENTE);
            linhas.saveAndFlush(linha);
            salvarComponentes(linha, calculado);
        } catch (ImportacaoInvalidaException e) {
            linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
            linha.setErroCodigo("LINHA_INVALIDA");
            linha.setErroMensagem(limitar(e.getMessage(), 255));
            linhas.save(linha);
        }
    }

    private void salvarComponentes(ImportacaoLinha linha, List<ConsumoService.Componente> componentes) {
        for (ConsumoService.Componente componente : componentes) {
            ImportacaoLinhaConsumo consumo = new ImportacaoLinhaConsumo();
            consumo.setLinha(linha);
            consumo.setInsumo(componente.getInsumo());
            consumo.setQuantidadePrevista(componente.getQuantidade());
            consumos.save(consumo);
        }
    }

    private ImportacaoLinha bloquear(Long loteId, Long linhaId) {
        ImportacaoLinha linha = linhas.findWithLockById(linhaId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Linha inexistente."
        ));

        if (!linha.getImportacao().getId().equals(loteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Linha não pertence ao registro.");
        }

        return linha;
    }

    private Importacao loteBloqueado(Long id) {
        return lotes.findWithLockById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Registro inexistente."
        ));
    }

    @Transactional
    public ImportacaoLinhaResponseDto personalizar(
            Long loteId, Long linhaId, ConsumoPersonalizacaoRequestDto pedido, Integer usuarioId) {
        buscarUsuario(usuarioId);
        Importacao lote = loteBloqueado(loteId);

        if (lote.getStatus() != StatusImportacaoEnum.PREVIA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Operação não permite mais edições.");
        }

        ImportacaoLinha linha = bloquear(loteId, linhaId);

        if (linha.getStatus() != StatusImportacaoLinhaEnum.PENDENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Somente itens válidos da prévia podem ser personalizados."
            );
        }

        if (!Objects.equals(linha.getVersao(), pedido.getVersao())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prévia alterada. Consulte novamente.");
        }

        List<ConsumoService.Componente> calculado;
        try {
            calculado = calculoConsumo.calcularPersonalizado(
                    linha.getProduto(), linha.getQuantidadeAtual(), pedido.getVariacoes()
            );
        } catch (ImportacaoInvalidaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        consumos.deleteAll(consumos.findByLinhaIdOrderByInsumoIdAsc(linhaId));
        consumos.flush();
        salvarComponentes(linha, calculado);
        linha.atualizarPrevia();
        linhas.saveAndFlush(linha);

        return detalhe(linha);
    }

    @Transactional
    public List<Long> iniciar(Long loteId, ImportacaoConfirmacaoRequestDto pedido, Integer usuarioId) {
        buscarUsuario(usuarioId);
        Importacao lote = loteBloqueado(loteId);
        List<ImportacaoLinha> itens = linhas.findByImportacaoIdOrderByNumeroLinhaAsc(loteId);

        if (lote.getStatus() == StatusImportacaoEnum.PREVIA) {
            Map<Long, Long> selecoes = new HashMap<>();

            for (ImportacaoSelecaoRequestDto selecao : pedido.getLinhas()) {
                if (selecoes.containsKey(selecao.getLinhaId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleção de linha repetida.");
                }

                selecoes.put(selecao.getLinhaId(), selecao.getVersao());
            }

            List<ImportacaoLinha> validas = new ArrayList<>();
            Set<Long> idsValidos = new HashSet<>();

            for (ImportacaoLinha item : itens) {
                if (item.getStatus() == StatusImportacaoLinhaEnum.PENDENTE) {
                    validas.add(item);
                    idsValidos.add(item.getId());
                }
            }

            if (validas.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro sem itens válidos.");
            }

            if (!selecoes.keySet().equals(idsValidos)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Confirme todas as linhas válidas da prévia.");
            }

            for (ImportacaoLinha linha : validas) {
                if (!Objects.equals(linha.getVersao(), selecoes.get(linha.getId()))) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Prévia alterada. Consulte novamente.");
                }
            }

            lote.setStatus(StatusImportacaoEnum.PROCESSANDO);
            lotes.saveAndFlush(lote);
        }

        List<Long> ids = new ArrayList<>();

        for (ImportacaoLinha item : itens) {
            ids.add(item.getId());
        }

        return ids;
    }

    @Transactional
    public ImportacaoResultadoResponseDto confirmarLinha(Long loteId, Long linhaId, Integer usuarioId) {
        ImportacaoLinha linha = bloquear(loteId, linhaId);

        if (linha.getStatus() != StatusImportacaoLinhaEnum.PENDENTE) {
            return resultado(linha, true);
        }

        if (linha.getImportacao().getStatus() != StatusImportacaoEnum.PROCESSANDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Registro não está em confirmação.");
        }

        if (!Boolean.TRUE.equals(linha.getProduto().getAtivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto desativado.");
        }

        List<ImportacaoLinhaConsumo> componentes = consumos.findByLinhaIdOrderByInsumoIdAsc(linhaId);

        for (ImportacaoLinhaConsumo componente : componentes) {
            estoque.registrarSaidaPorConsumo(componente, usuarioId);
        }

        linha.setStatus(StatusImportacaoLinhaEnum.PROCESSADA);
        linha.setProcessadoEm(LocalDateTime.now());
        linha.setErroCodigo(null);
        linha.setErroMensagem(null);
        linhas.saveAndFlush(linha);

        return resultado(linha, false);
    }

    @Transactional
    public ImportacaoResultadoResponseDto registrarFalha(Long loteId, Long linhaId) {
        ImportacaoLinha linha = bloquear(loteId, linhaId);

        if (linha.getStatus() == StatusImportacaoLinhaEnum.PENDENTE) {
            linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
            linha.setErroCodigo("FALHA_PROCESSAMENTO");
            linha.setErroMensagem("Não foi possível processar este item. Nenhum ingrediente dele foi descontado.");
            linhas.saveAndFlush(linha);
        }

        return resultado(linha, false);
    }

    @Transactional
    public void encerrar(Long loteId) {
        Importacao lote = loteBloqueado(loteId);

        if (linhas.countByImportacaoIdAndStatus(loteId, StatusImportacaoLinhaEnum.PENDENTE) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Confirmação ainda em processamento.");
        }

        if (lote.getStatus() != StatusImportacaoEnum.CONCLUIDO) {
            lote.setStatus(StatusImportacaoEnum.CONCLUIDO);
            lotes.save(lote);
        }
    }

    private ImportacaoResultadoResponseDto resultado(ImportacaoLinha linha, boolean repeticao) {
        return new ImportacaoResultadoResponseDto(
                linha.getId(),
                repeticao && linha.getStatus() == StatusImportacaoLinhaEnum.PROCESSADA,
                linha.getErroCodigo(),
                linha.getErroMensagem(),
                detalhe(linha)
        );
    }

    private ImportacaoLinhaResponseDto detalhe(ImportacaoLinha linha) {
        return ImportacaoMapper.toResponseDto(
                linha, consumos.findByLinhaIdOrderByInsumoIdAsc(linha.getId())
        );
    }

    public Usuario buscarUsuario(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário inexistente.");
        }

        return usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário inexistente."
        ));
    }

    static String limitar(String texto, int max) {
        if (texto == null) {
            return null;
        }

        return texto.substring(0, Math.min(texto.length(), max));
    }
}
