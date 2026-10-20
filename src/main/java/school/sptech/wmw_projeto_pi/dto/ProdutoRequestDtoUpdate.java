package school.sptech.wmw_projeto_pi.dto;

import jakarta.persistence.ManyToOne;
import school.sptech.wmw_projeto_pi.entity.Usuario;
import school.sptech.wmw_projeto_pi.enums.TamanhoEnum;

import java.time.LocalDateTime;

public class ProdutoRequestDtoUpdate {
    private String nome;
    private Boolean ativo;
    private TamanhoEnum tamanho;

    public ProdutoRequestDtoUpdate() {
    }

    public ProdutoRequestDtoUpdate(String nome, Boolean ativo, TamanhoEnum tamanho) {
        this.nome = nome;
        this.ativo = ativo;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public TamanhoEnum getTamanho() {
        return tamanho;
    }

    public void setTamanho(TamanhoEnum tamanho) {
        this.tamanho = tamanho;
    }
}
