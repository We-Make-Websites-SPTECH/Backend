package school.sptech.wmw_projeto_pi.dto.produto;


import java.time.LocalDateTime;
import school.sptech.wmw_projeto_pi.enums.TipoProdutoEnum;

public class ProdutoResponseDtoGetAll {
    private Integer id;
    private String nome;
    private Boolean ativo;
    private TipoProdutoEnum tipo;
    private Integer tamanho;
    private LocalDateTime createdAt;
    private ProdutoUsuarioResponseDto createdBy;
    private LocalDateTime updatedAt;
    private ProdutoUsuarioResponseDto updatedBy;

    public ProdutoResponseDtoGetAll() {
    }

    public ProdutoResponseDtoGetAll(Integer id, String nome, Boolean ativo, TipoProdutoEnum tipo, Integer tamanho, LocalDateTime createdAt, ProdutoUsuarioResponseDto createdBy, LocalDateTime updatedAt, ProdutoUsuarioResponseDto updatedBy) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.tipo = tipo;
        this.tamanho = tamanho;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public TipoProdutoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoProdutoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ProdutoUsuarioResponseDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ProdutoUsuarioResponseDto createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProdutoUsuarioResponseDto getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ProdutoUsuarioResponseDto updatedBy) {
        this.updatedBy = updatedBy;
    }
}
