package school.sptech.wmw_projeto_pi.dto.insumo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public class InsumoRequestDtoUpdate {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    @Pattern(regexp = "g|ml|un", message = "Unidade de medida deve ser g, ml ou un")
    private String unidadeMedida;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal saldo;

    public InsumoRequestDtoUpdate() {
    }

    public InsumoRequestDtoUpdate(String nome, String unidadeMedida, BigDecimal saldo) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.saldo = saldo;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
