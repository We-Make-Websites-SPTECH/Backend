package school.sptech.wmw_projeto_pi.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.MovimentacaoEstoque;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.AlteracaoUnidadeComMovimentacaoException;
import school.sptech.wmw_projeto_pi.exception.DadosInsumoConflitantesException;
import school.sptech.wmw_projeto_pi.exception.InsumoDesativadoException;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.UsuarioInexistenteOuInativoException;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.MovimentacaoEstoqueRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InsumoService {
    private final InsumoRepository insumoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public InsumoService(InsumoRepository insumoRepository, UsuarioRepository usuarioRepository,
                         MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
                         MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.insumoRepository = insumoRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    public List<Insumo> getAll(String busca, Boolean ativo) {
        List<Insumo> insumos = insumoRepository.findByNomeContainingIgnoreCaseOrderByNomeAscIdAsc(busca.trim());

        if (ativo == null) {
            return insumos;
        }

        List<Insumo> insumosFiltrados = new ArrayList<>();
        for (Insumo insumo : insumos) {
            if (ativo.equals(insumo.getAtivo())) {
                insumosFiltrados.add(insumo);
            }
        }
        return insumosFiltrados;
    }

    public Insumo getById(Integer id) {
        Optional<Insumo> insumoOptional = insumoRepository.findById(id);
        if (insumoOptional.isEmpty()) throw new InsumoNotFoundException();

        return insumoOptional.get();
    }

    @Transactional
    public Insumo create(Insumo insumo, Integer idUsuario) {
        Usuario usuario = findUser(idUsuario);
        BigDecimal saldoInicial = insumo.getQuantidadeAtual();

        insumo.setNome(insumo.getNome().trim());
        insumo.setQuantidadeAtual(BigDecimal.ZERO);
        insumo.setAtivo(true);
        insumo.setCreatedBy(usuario);

        salvarInsumo(insumo);
        movimentacaoEstoqueService.registrar(insumo.getId(), usuario, saldoInicial, true);

        return insumo;
    }

    @Transactional
    public Insumo update(Integer id, Insumo insumoRequest, Integer idUsuario) {
        Usuario usuario = findUser(idUsuario);
        Insumo insumoExistente = buscarParaAtualizar(id);

        if (!Boolean.TRUE.equals(insumoExistente.getAtivo())) throw new InsumoDesativadoException();

        boolean unidadeAlterada = insumoRequest.getUnidadeMedida() != insumoExistente.getUnidadeMedida();
        if (unidadeAlterada && movimentacaoEstoqueRepository.existsByInsumoIdAndQuantidadeGreaterThan(id, BigDecimal.ZERO)) {
            throw new AlteracaoUnidadeComMovimentacaoException();
        }

        BigDecimal diferencaSaldo = insumoRequest.getQuantidadeAtual().subtract(insumoExistente.getQuantidadeAtual());
        insumoExistente.setNome(insumoRequest.getNome().trim());
        insumoExistente.setUnidadeMedida(insumoRequest.getUnidadeMedida());

        salvarInsumo(insumoExistente);
        movimentacaoEstoqueService.registrar(id, usuario, diferencaSaldo, false);

        return insumoExistente;
    }

    @Transactional
    public void desativar(Integer id) {
        Insumo insumo = buscarParaAtualizar(id);
        insumo.setAtivo(false);

        insumoRepository.save(insumo);
    }

    public List<MovimentacaoEstoque> getMovimentacoes(Integer id) {
        getById(id);

        return movimentacaoEstoqueRepository.findByInsumoIdOrderByDataHoraAscIdAsc(id);
    }

    // AUXILIARES

    public Usuario findUser(Integer id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) throw new UsuarioInexistenteOuInativoException();

        Usuario usuario = usuarioOptional.get();
        if (!Boolean.TRUE.equals(usuario.getAtivo())) throw new UsuarioInexistenteOuInativoException();

        return usuario;
    }

    public void validarUsuario(Integer idUsuario) {
        findUser(idUsuario);
    }

    private Insumo buscarParaAtualizar(Integer id) {
        Optional<Insumo> insumoOptional = insumoRepository.buscarParaAtualizar(id);
        if (insumoOptional.isEmpty()) throw new InsumoNotFoundException();

        return insumoOptional.get();
    }

    private void salvarInsumo(Insumo insumo) {
        try {
            insumoRepository.saveAndFlush(insumo);
        } catch (DataIntegrityViolationException exception) {
            throw new DadosInsumoConflitantesException();
        }
    }
}
