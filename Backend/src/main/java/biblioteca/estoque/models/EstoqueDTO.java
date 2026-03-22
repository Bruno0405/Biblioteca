package biblioteca.estoque.models;

public class EstoqueDTO {

    private Integer idEstoque;
    private Integer idLivro;
    private Integer quantidadeTotal;
    private Integer quantidadeReservada;
    private Integer quantidadeEmprestada;
    private Integer quantidadeDanificada;
    private Integer estoqueMinimo;

    public Integer getIdEstoque() {
        return idEstoque;
    }
    public void setIdEstoque(Integer idEstoque) {
        this.idEstoque = idEstoque;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal; }
    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public Integer getQuantidadeReservada() {
        return quantidadeReservada;
    }
    public void setQuantidadeReservada(Integer quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public Integer getQuantidadeEmprestada() {
        return quantidadeEmprestada;
    }
    public void setQuantidadeEmprestada(Integer quantidadeEmprestada) {
        this.quantidadeEmprestada = quantidadeEmprestada;
    }

    public Integer getQuantidadeDanificada() {
        return quantidadeDanificada;
    }
    public void setQuantidadeDanificada(Integer quantidadeDanificada) {
        this.quantidadeDanificada = quantidadeDanificada;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }
    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }
}