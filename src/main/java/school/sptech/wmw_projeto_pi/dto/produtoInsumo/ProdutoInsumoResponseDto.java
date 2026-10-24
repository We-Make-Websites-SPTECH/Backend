package school.sptech.wmw_projeto_pi.dto.produtoInsumo;

import school.sptech.wmw_projeto_pi.dto.produto.ProdutoUsuarioResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoInsumoResponseDto {
    private Integer id;
    private ProdutoResumoResponseDto produto;
    private InsumoResumoResponseDto insumo;
    private BigDecimal quantidade;
    private LocalDateTime createdAt;
    private ProdutoUsuarioResponseDto createdBy;
    private LocalDateTime updatedAt;
    private ProdutoUsuarioResponseDto updatedBy;

    public ProdutoInsumoResponseDto() {
    }

    public ProdutoInsumoResponseDto(Integer id, ProdutoResumoResponseDto produto, InsumoResumoResponseDto insumo, BigDecimal quantidade, LocalDateTime createdAt, ProdutoUsuarioResponseDto createdBy, LocalDateTime updatedAt, ProdutoUsuarioResponseDto updatedBy) {
        this.id = id;
        this.produto = produto;
        this.insumo = insumo;
        this.quantidade = quantidade;
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

    public ProdutoResumoResponseDto getProduto() {
        return produto;
    }

    public void setProduto(ProdutoResumoResponseDto produto) {
        this.produto = produto;
    }

    public InsumoResumoResponseDto getInsumo() {
        return insumo;
    }

    public void setInsumo(InsumoResumoResponseDto insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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
