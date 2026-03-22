package biblioteca.estoque.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Estoque")
public class Estoque extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Integer idEstoque;

    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;

    @Column(name = "quantidade_reservada")
    private Integer quantidadeReservada;

    @Column(name = "quantidade_emprestada")
    private Integer quantidadeEmprestada;

    @Column(name = "quantidade_danificada")
    private Integer quantidadeDanificada;

    @Column(name = "estoque_minimo")
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
        return quantidadeTotal;
    }
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