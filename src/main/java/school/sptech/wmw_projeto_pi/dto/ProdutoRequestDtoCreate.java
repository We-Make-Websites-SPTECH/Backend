package school.sptech.wmw_projeto_pi.dto;

import jakarta.persistence.ManyToOne;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

import java.time.LocalDateTime;

public class ProdutoRequestDtoCreate {
    private String nome;
    private TamanhoEnum tamanho;

    public ProdutoRequestDtoCreate() {
    }

    public ProdutoRequestDtoCreate(String nome, TamanhoEnum tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TamanhoEnum getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoEnum tamanho) {
        this.tamanho = tamanho;
    }
}
