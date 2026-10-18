package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Boolean existsByEmail (String email);
    Optional<Usuario> findByEmail(String email);
}
