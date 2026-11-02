package school.sptech.wmw_projeto_pi.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.dto.produtoInsumo.*;
import school.sptech.wmw_projeto_pi.entity.Insumo;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.ProdutoInsumo;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.InsumoNotFoundException;
import school.sptech.wmw_projeto_pi.exception.InsumoDesativadoException;
import school.sptech.wmw_projeto_pi.exception.ProductNotFoundException;
import school.sptech.wmw_projeto_pi.exception.ReceitaNotFoundException;
import school.sptech.wmw_projeto_pi.exception.UserNotFoundException;
import school.sptech.wmw_projeto_pi.mapper.ProdutoInsumoMapper;
import school.sptech.wmw_projeto_pi.repository.InsumoRepository;
import school.sptech.wmw_projeto_pi.repository.ProdutoInsumoRepository;
import school.sptech.wmw_projeto_pi.repository.ProdutoRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoInsumoService {
    private final ProdutoInsumoRepository produtoInsumoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InsumoRepository insumoRepository;

    public ProdutoInsumoService(ProdutoInsumoRepository produtoInsumoRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository, InsumoRepository insumoRepository) {
        this.produtoInsumoRepository = produtoInsumoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.insumoRepository = insumoRepository;
    }

    public List<ReceitaResponseDto> getAllReceitas() {
        List<ProdutoInsumo> listaProdutoInsumo = produtoInsumoRepository.findAll();

        List<ReceitaResponseDto> receitas = new ArrayList<>();
        List<Integer> produtosAdicionados = new ArrayList<>();

        for (ProdutoInsumo pi : listaProdutoInsumo) {
            Integer produtoId = pi.getProduto().getId();

            if (!produtosAdicionados.contains(produtoId)) {
                List<ProdutoInsumo> ingredientesReceita = new ArrayList<>();

                for (ProdutoInsumo ingrediente : listaProdutoInsumo) {
                    if (ingrediente.getProduto().getId().equals(produtoId)) {
                        ingredientesReceita.add(ingrediente);
                    }
                }

                receitas.add(ProdutoInsumoMapper.toReceitaResponseDto(ingredientesReceita));
                produtosAdicionados.add(produtoId);
            }
        }
        return receitas;
    }

    public ReceitaResponseDto getReceitaByProdutoId(Integer produtoId) {
        validarProduto(produtoId);

        List<ProdutoInsumo> ingredientes = produtoInsumoRepository.findByProdutoId(produtoId);

        if (ingredientes.isEmpty()) throw new ReceitaNotFoundException();

        return ProdutoInsumoMapper.toReceitaResponseDto(ingredientes);
    }

    @Transactional
    public ReceitaResponseDto create(ProdutoInsumoRequestDto receitaRequest, Integer idUsuario) {
        validarProduto(receitaRequest.getProdutoId());
        validarUsuario(idUsuario);

        Produto produto = produtoRepository.findById(receitaRequest.getProdutoId())
                .orElseThrow(ProductNotFoundException::new);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(UserNotFoundException::new);

        List<ProdutoInsumo> listaProdutoInsumo = new ArrayList<>();

        for (ProdutoInsumoItemRequestDto ingredienteRequest : receitaRequest.getIngredientes()) {
            validarInsumo(ingredienteRequest.getInsumoId());

            Insumo insumo = insumoRepository.findById(ingredienteRequest.getInsumoId())
                    .orElseThrow(InsumoNotFoundException::new);

            ProdutoInsumo entity = ProdutoInsumoMapper.toEntity(
                    produto,
                    insumo,
                    ingredienteRequest.getQuantidade(),
                    usuario
            );

            listaProdutoInsumo.add(entity);
        }

        List<ProdutoInsumo> receitasSalvas = produtoInsumoRepository.saveAll(listaProdutoInsumo);
        return ProdutoInsumoMapper.toReceitaResponseDto(receitasSalvas);
    }


    @Transactional
    public ReceitaResponseDto updateReceita(Integer produtoId, ProdutoInsumoUpdateRequestDto receitaRequest,Integer idUsuario) {
        validarProduto(produtoId);
        validarUsuario(idUsuario);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(ProductNotFoundException::new);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(UserNotFoundException::new);

        List<ProdutoInsumo> ingredientesAtuais = produtoInsumoRepository.findByProdutoId(produtoId);

        List<ProdutoInsumo> ingredientesParaSalvar = new ArrayList<>();
        List<ProdutoInsumo> ingredientesParaRemover = new ArrayList<>();

        // Copia a lista para identificar quais ingredientes são novos
        List<ProdutoInsumoItemRequestDto> ingredientesNovos = new ArrayList<>(receitaRequest.getIngredientes());

        for (ProdutoInsumoItemRequestDto ingrediente : ingredientesNovos) {
            validarInsumo(ingrediente.getInsumoId());
        }

        // Compara os ingredientes atuais com os recebidos
        for (ProdutoInsumo ingredienteAtual : ingredientesAtuais) {
            boolean encontrado = false;

            for (int i = 0; i < ingredientesNovos.size(); i++) {
                ProdutoInsumoItemRequestDto ingredienteRequest = ingredientesNovos.get(i);

                if (ingredienteAtual.getInsumo().getId().equals(ingredienteRequest.getInsumoId())) {

                    // O ingrediente continua na receita: atualiza a quantidade
                    ingredienteAtual.setQuantidade(ingredienteRequest.getQuantidade());
                    ingredienteAtual.setUpdatedBy(usuario);

                    ingredientesParaSalvar.add(ingredienteAtual);

                    // Remove da lista temporária para não cadastrá-lo novamente
                    ingredientesNovos.remove(i);

                    encontrado = true;
                    break;
                }
            }

            // Se não veio na requisição, foi removido da receita
            if (!encontrado) {
                ingredientesParaRemover.add(ingredienteAtual);
            }
        }

        // Exclui somente as associações removidas da receita
        produtoInsumoRepository.deleteAll(ingredientesParaRemover);

        // Os ingredientes que sobraram na lista são novos
        for (ProdutoInsumoItemRequestDto ingredienteRequest : ingredientesNovos) {
            validarInsumo(ingredienteRequest.getInsumoId());

            Insumo insumo = insumoRepository.findById(ingredienteRequest.getInsumoId())
                    .orElseThrow(InsumoNotFoundException::new);

            ProdutoInsumo novoIngrediente = ProdutoInsumoMapper.toEntity(produto, insumo, ingredienteRequest.getQuantidade(), usuario);
            ingredientesParaSalvar.add(novoIngrediente);
        }

        produtoInsumoRepository.saveAll(ingredientesParaSalvar);

        // Busca a composição final para retornar a receita completa
        List<ProdutoInsumo> receitaAtualizada = produtoInsumoRepository.findByProdutoId(produtoId);

        return ProdutoInsumoMapper.toReceitaResponseDto(receitaAtualizada);
    }


    // VALIDAÇÕES
    public void validarProduto(Integer produtoId) {
        if (!produtoRepository.existsById(produtoId)) throw new ProductNotFoundException();
    }

    public void validarUsuario(Integer usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) throw new UserNotFoundException();
    }

    public void validarInsumo(Integer insumoId) {
        Insumo insumo = insumoRepository.buscarParaAtualizar(insumoId).orElseThrow(InsumoNotFoundException::new);
        if (!Boolean.TRUE.equals(insumo.getAtivo())) {
            throw new InsumoDesativadoException();
        }
    }

}
