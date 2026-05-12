package biblioteca.movimentacao.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import biblioteca.livros.data.Livro;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Movimentacao_estoque")
public class MovimentacaoEstoque extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Integer idMovimentacao;

    @Column(name = "id_livro")
    private Integer idLivro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livro", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Livro livro;

    @Column(name = "tipo_movimentacao")
    private Character tipoMovimentacao;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "data_movimentacao")
    private LocalDate dataMovimentacao;

    @Column(name = "motivo")
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

    public Livro getLivro() {
        return livro;
    }
    public void setLivro(Livro livro) {
        this.livro = livro;
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