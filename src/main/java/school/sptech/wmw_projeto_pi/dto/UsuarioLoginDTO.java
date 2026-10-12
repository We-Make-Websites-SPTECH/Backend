package school.sptech.wmw_projeto_pi.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;

@Entity
public class UsuarioLoginDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
