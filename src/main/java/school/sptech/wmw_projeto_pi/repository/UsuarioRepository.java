package school.sptech.wmw_projeto_pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.wmw_projeto_pi.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Boolean existsByEmail (String email);
}
