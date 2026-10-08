package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.*;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

import java.time.LocalDate;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private Boolean ativo;
    private TamanhoEnum tamanho;
    private LocalDate createdAt;
    @ManyToOne
    private Usuario createdBy;
    private LocalDate updatedAt;
    @ManyToOne
    private Usuario updatedBy;

    public Produto() {
    }



    public Usuario getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Usuario updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

}
