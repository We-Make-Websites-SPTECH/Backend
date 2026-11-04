package school.sptech.wmw_projeto_pi.dto.insumo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class InsumoRequestDtoUpdate {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    @Pattern(regexp = "g|un", message = "Unidade de medida deve ser g ou un")
    private String unidadeMedida;

    @NotNull
    @Min(0)
    @Max(99)
    private Integer percentualPerda;

    public InsumoRequestDtoUpdate() {
    }

    public InsumoRequestDtoUpdate(String nome, String unidadeMedida, Integer percentualPerda) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.percentualPerda = percentualPerda;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Integer getPercentualPerda() {
        return percentualPerda;
    }

    public void setPercentualPerda(Integer percentualPerda) {
        this.percentualPerda = percentualPerda;
    }
}
