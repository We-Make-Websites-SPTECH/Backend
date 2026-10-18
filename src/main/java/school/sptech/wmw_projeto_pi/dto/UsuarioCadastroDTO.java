package school.sptech.wmw_projeto_pi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioCadastroDTO {
    @NotBlank
    private String nome;
    @Email
    @NotBlank
    @Size(min=3, max=255)
    private String email;
    @NotBlank
    @Size(min=8, max=64)
    @Pattern(regexp = ".*[-!@#$%&*+=_?;:].*", message = "A senha deve conter caracteres especiais.")
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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
