package biblioteca.movimentacao_estoque.models;

import java.time.LocalDate;

public class MovimentacaoEstoqueDTO {

    private Integer idMovimentacao;
    private Integer idLivro;
    private Character tipoMovimentacao;
    private Integer quantidade;
    private LocalDate dataMovimentacao;
    private String motivo;

    public Integer getIdMovimentacao() {
        return idMovimentacao;
    }
    public void setIdMovimentacao(Integer idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Character getTipoMovimentacao() {
        return tipoMovimentacao;
    }
    public void setTipoMovimentacao(Character tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataMovimentacao() {
        return dataMovimentacao;
    }
    public void setDataMovimentacao(LocalDate dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}