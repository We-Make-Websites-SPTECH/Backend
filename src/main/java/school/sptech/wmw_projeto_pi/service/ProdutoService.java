package school.sptech.wmw_projeto_pi.service;

import org.springframework.stereotype.Service;
import school.sptech.wmw_projeto_pi.entity.Produto;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.exception.NoContentException;
import school.sptech.wmw_projeto_pi.exception.ProductNotFoundException;
import school.sptech.wmw_projeto_pi.exception.UserNotFoundException;
import school.sptech.wmw_projeto_pi.repository.ProdutoRepository;
import school.sptech.wmw_projeto_pi.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Produto> getAll() {
        List<Produto> produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) throw new NoContentException();

        return produtos;
    }

    public Produto getById(Integer id) {
        Optional<Produto> produtoOptional = produtoRepository.findById(id);
        if (produtoOptional.isEmpty()) throw new ProductNotFoundException();

        return produtoOptional.get();
    }

    public Produto create(Produto produto, Integer idUsuario) {
        Usuario usuario = findUser(idUsuario);
        produto.setAtivo(true);
        produto.setCreatedBy(usuario);
        produto.setUpdatedBy(usuario);

        return produtoRepository.save(produto);
    }

    public Produto update(Integer id, Produto produtoRequest, Integer idUsuario) {
        Produto produtoExistente = getById(id);
        Usuario usuario = findUser(idUsuario);

        produtoRequest.setCreatedBy(produtoExistente.getCreatedBy());
        produtoRequest.setCreatedAt(produtoExistente.getCreatedAt());
        produtoRequest.setUpdatedBy(usuario);
        produtoRequest.setId(id);

        return produtoRepository.save(produtoRequest);
    }




    public Usuario findUser(Integer id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) throw new UserNotFoundException();
        Usuario usuario = usuarioOptional.get();

        return usuario;
    }
}
