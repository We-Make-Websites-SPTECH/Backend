package school.sptech.wmw_projeto_pi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    //private Cargo cargo;
    private String nome;
    private String senha;
    private Boolean ativo;
    // private Usuario createdBy;
    // private LocalDate createdAt;
    // private Usuario updatedBy;
    // private Localdate updatedAt;

}
