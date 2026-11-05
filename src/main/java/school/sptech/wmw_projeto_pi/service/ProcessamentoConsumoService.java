package school.sptech.wmw_projeto_pi.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.wmw_projeto_pi.dto.interno.ComponenteConsumoDto;
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
import java.util.List;
import java.util.Optional;

@Service
public class ProcessamentoConsumoService {
    private final ImportacaoRepository lotesRepository;
    private final ImportacaoLinhaRepository linhasRepository;
    private final ImportacaoLinhaConsumoRepository consumosRepository;
    private final ProdutoRepository produtosRepository;
    private final UsuarioRepository usuariosRepository;
    private final ConsumoService calculoConsumoService;
    private final EstoqueService estoqueService;

    public ProcessamentoConsumoService(
            ImportacaoRepository lotesRepository,
            ImportacaoLinhaRepository linhasRepository,
            ImportacaoLinhaConsumoRepository consumosRepository,
            ProdutoRepository produtosRepository,
            UsuarioRepository usuariosRepository,
            ConsumoService calculoConsumoService,
            EstoqueService estoqueService) {
        this.lotesRepository = lotesRepository;
        this.linhasRepository = linhasRepository;
        this.consumosRepository = consumosRepository;
        this.produtosRepository = produtosRepository;
        this.usuariosRepository = usuariosRepository;
        this.calculoConsumoService = calculoConsumoService;
        this.estoqueService = estoqueService;
    }

    public void prepararLinhaTxt(ImportacaoLinha linha, Integer produtoId, String nome, Integer quantidade) {
        prepararLinha(linha, produtoId, nome, quantidade, false);
    }

    public void prepararLinhaManual(ImportacaoLinha linha, Integer produtoId, Integer quantidade) {
        prepararLinha(linha, produtoId, "Produto ID " + produtoId, quantidade, true);
    }

    private void prepararLinha(
            ImportacaoLinha linha, Integer produtoId, String nome, Integer quantidade, Boolean origemManual) {
        linha.setNomeAtual(limitarTexto(nome, 255));
        linha.setQuantidadeAtual(quantidade);

        try {
            validarDadosLinha(produtoId, nome, quantidade);
            Produto produto = buscarProduto(produtoId);
            linha.setProduto(produto);

            if (origemManual) {
                linha.setNomeAtual(limitarTexto(produto.getNome(), 255));
            } else {
                validarNomeProduto(produto, nome);
            }

            List<ComponenteConsumoDto> consumoCalculado = calculoConsumoService.calcularConsumo(produto, quantidade);

            if (!origemManual) {
                linha.setNomeAtual(limitarTexto(produto.getNome(), 255));
            }

            linha.setStatus(StatusImportacaoLinhaEnum.PENDENTE);
            linhasRepository.save(linha);
            salvarComponentes(linha, consumoCalculado);
        } catch (ImportacaoInvalidaException e) {
            linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
            linha.setErroCodigo("LINHA_INVALIDA");
            linha.setErroMensagem(limitarTexto(e.getMessage(), 255));
            linhasRepository.save(linha);
        }
    }

    private void salvarComponentes(ImportacaoLinha linha, List<ComponenteConsumoDto> componentes) {
        for (ComponenteConsumoDto componente : componentes) {
            ImportacaoLinhaConsumo consumo = new ImportacaoLinhaConsumo();
            consumo.setLinha(linha);
            consumo.setInsumo(componente.getInsumo());
            consumo.setQuantidadePrevista(componente.getQuantidade());
            consumosRepository.save(consumo);
        }
    }

    private ImportacaoLinha buscarLinha(Long loteId, Long linhaId) {
        Optional<ImportacaoLinha> linhaOptional = linhasRepository.findById(linhaId);

        if (linhaOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Linha inexistente.");
        }

        ImportacaoLinha linha = linhaOptional.get();

        if (!linha.getImportacao().getId().equals(loteId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Linha não pertence ao registro.");
        }

        return linha;
    }

    private Importacao buscarRegistro(Long id) {
        Optional<Importacao> registroOptional = lotesRepository.findById(id);

        if (registroOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro inexistente.");
        }

        return registroOptional.get();
    }

    @Transactional
    public ImportacaoLinhaResponseDto personalizar(
            Long loteId, Long linhaId, ConsumoPersonalizacaoRequestDto pedido, Integer usuarioId) {
        buscarUsuario(usuarioId);
        Importacao lote = buscarRegistro(loteId);

        if (lote.getStatus() != StatusImportacaoEnum.PREVIA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Operação não permite mais edições.");
        }

        ImportacaoLinha linha = buscarLinha(loteId, linhaId);

        if (linha.getStatus() != StatusImportacaoLinhaEnum.PENDENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Somente itens válidos da prévia podem ser personalizados."
            );
        }

        List<ComponenteConsumoDto> consumoCalculado = calculoConsumoService.calcularPersonalizado(
                linha.getProduto(), linha.getQuantidadeAtual(), pedido.getVariacoes()
        );

        List<ImportacaoLinhaConsumo> consumoAnterior = consumosRepository.findByLinhaIdOrderByInsumoIdAsc(linhaId);
        consumosRepository.deleteAll(consumoAnterior);
        consumosRepository.flush();
        salvarComponentes(linha, consumoCalculado);
        linha.atualizarPrevia();
        linhasRepository.save(linha);

        return montarDetalheLinha(linha);
    }

    @Transactional
    public List<Long> iniciarConfirmacao(Long loteId, ImportacaoConfirmacaoRequestDto pedido, Integer usuarioId) {
        buscarUsuario(usuarioId);
        Importacao lote = buscarRegistro(loteId);
        List<ImportacaoLinha> itens = linhasRepository.findByImportacaoIdOrderByNumeroLinhaAsc(loteId);

        if (lote.getStatus() == StatusImportacaoEnum.PREVIA) {
            validarSelecaoConfirmacao(itens, pedido.getLinhas());
            lote.setStatus(StatusImportacaoEnum.PROCESSANDO);
            lotesRepository.save(lote);
        }

        List<Long> ids = new ArrayList<>();

        for (ImportacaoLinha item : itens) {
            ids.add(item.getId());
        }

        return ids;
    }

    @Transactional
    public ImportacaoResultadoResponseDto confirmarLinha(Long loteId, Long linhaId, Integer usuarioId) {
        ImportacaoLinha linha = buscarLinha(loteId, linhaId);

        if (linha.getStatus() != StatusImportacaoLinhaEnum.PENDENTE) {
            return montarResultado(linha, true);
        }

        if (linha.getImportacao().getStatus() != StatusImportacaoEnum.PROCESSANDO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Registro não está em confirmação.");
        }

        if (!Boolean.TRUE.equals(linha.getProduto().getAtivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto desativado.");
        }

        List<ImportacaoLinhaConsumo> componentes = consumosRepository.findByLinhaIdOrderByInsumoIdAsc(linhaId);

        for (ImportacaoLinhaConsumo componente : componentes) {
            estoqueService.registrarSaidaPorConsumo(componente, usuarioId);
        }

        linha.setStatus(StatusImportacaoLinhaEnum.PROCESSADA);
        linha.setProcessadoEm(LocalDateTime.now());
        linha.setErroCodigo(null);
        linha.setErroMensagem(null);
        linhasRepository.save(linha);

        return montarResultado(linha, false);
    }

    @Transactional
    public ImportacaoResultadoResponseDto registrarFalha(Long loteId, Long linhaId) {
        ImportacaoLinha linha = buscarLinha(loteId, linhaId);

        if (linha.getStatus() == StatusImportacaoLinhaEnum.PENDENTE) {
            linha.setStatus(StatusImportacaoLinhaEnum.INVALIDA);
            linha.setErroCodigo("FALHA_PROCESSAMENTO");
            linha.setErroMensagem("Não foi possível processar este item. Nenhum ingrediente dele foi descontado.");
            linhasRepository.save(linha);
        }

        return montarResultado(linha, false);
    }

    @Transactional
    public void concluirRegistro(Long loteId) {
        Importacao lote = buscarRegistro(loteId);

        if (linhasRepository.countByImportacaoIdAndStatus(loteId, StatusImportacaoLinhaEnum.PENDENTE) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Confirmação ainda em processamento.");
        }

        if (lote.getStatus() != StatusImportacaoEnum.CONCLUIDO) {
            lote.setStatus(StatusImportacaoEnum.CONCLUIDO);
            lotesRepository.save(lote);
        }
    }

    private ImportacaoResultadoResponseDto montarResultado(ImportacaoLinha linha, Boolean repeticao) {
        return new ImportacaoResultadoResponseDto(
                linha.getId(),
                repeticao && linha.getStatus() == StatusImportacaoLinhaEnum.PROCESSADA,
                linha.getErroCodigo(),
                linha.getErroMensagem(),
                montarDetalheLinha(linha)
        );
    }

    private ImportacaoLinhaResponseDto montarDetalheLinha(ImportacaoLinha linha) {
        return ImportacaoMapper.toResponseDto(
                linha, consumosRepository.findByLinhaIdOrderByInsumoIdAsc(linha.getId())
        );
    }

    public Usuario buscarUsuario(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário inexistente.");
        }

        Optional<Usuario> usuarioOptional = usuariosRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário inexistente.");
        }

        return usuarioOptional.get();
    }

    private void validarDadosLinha(Integer produtoId, String nome, Integer quantidade) {
        if (produtoId == null || quantidade == null || quantidade <= 0 || nome == null || nome.isBlank()) {
            throw new ImportacaoInvalidaException("ID, nome e quantidade inteira positiva são obrigatórios.");
        }
    }

    private Produto buscarProduto(Integer produtoId) {
        Optional<Produto> produtoOptional = produtosRepository.findById(produtoId);

        if (produtoOptional.isEmpty()) {
            throw new ImportacaoInvalidaException("Produto inexistente.");
        }

        return produtoOptional.get();
    }

    private void validarNomeProduto(Produto produto, String nomeInformado) {
        if (!produto.getNome().trim().equalsIgnoreCase(nomeInformado.trim())) {
            throw new ImportacaoInvalidaException("Nome não corresponde ao ID do produto.");
        }
    }

    private void validarSelecaoConfirmacao(
            List<ImportacaoLinha> linhasRegistro, List<ImportacaoSelecaoRequestDto> selecoes) {
        List<Long> idsSelecionados = new ArrayList<>();

        for (ImportacaoSelecaoRequestDto selecao : selecoes) {
            if (idsSelecionados.contains(selecao.getLinhaId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleção de linha repetida.");
            }

            idsSelecionados.add(selecao.getLinhaId());
        }

        List<ImportacaoLinha> linhasValidas = new ArrayList<>();

        for (ImportacaoLinha linha : linhasRegistro) {
            if (linha.getStatus() == StatusImportacaoLinhaEnum.PENDENTE) {
                linhasValidas.add(linha);
            }
        }

        if (linhasValidas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro sem itens válidos.");
        }

        if (idsSelecionados.size() != linhasValidas.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Confirme todas as linhas válidas da prévia.");
        }

        for (ImportacaoLinha linha : linhasValidas) {
            if (!idsSelecionados.contains(linha.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Confirme todas as linhas válidas da prévia.");
            }
        }

    }

    static String limitarTexto(String texto, Integer tamanhoMaximo) {
        if (texto == null) {
            return null;
        }

        return texto.substring(0, Math.min(texto.length(), tamanhoMaximo));
    }
}
