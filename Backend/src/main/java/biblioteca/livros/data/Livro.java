package biblioteca.livros.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import biblioteca.autores.data.Autor;
import biblioteca.estoque.data.Estoque;
import biblioteca.fotos.data.Foto;
import biblioteca.generos.data.Genero;
import biblioteca.movimentacao.data.MovimentacaoEstoque;
import biblioteca.reservas.data.Reserva;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Livros")
public class Livro extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "nome_livro")
    private String nomeLivro;

    @Column(name = "isnb")
    private String isnb;

    @Column(name = "editora")
    private String editora;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "sinopse")
    private String sinopse;

    @Column(name = "localizacao_fisica")
    private String localizacaoFisica;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Livro_Autor",
        joinColumns = @JoinColumn(name = "id_livro"),
        inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    @JsonIgnoreProperties("livros")
    private Set<Autor> autores = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Genero_livro",
        joinColumns = @JoinColumn(name = "id_livro"),
        inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    @JsonIgnoreProperties("livros")
    private Set<Genero> generos = new HashSet<>();

    @OneToOne(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("livro")
    private Estoque estoque;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("livro")
    private Set<Foto> fotos = new HashSet<>();

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("livro")
    private Set<MovimentacaoEstoque> movimentacoes = new HashSet<>();

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("livro")
    private Set<Reserva> reservas = new HashSet<>();

    public Integer getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getIsnb() {
        return isnb;
    }

    public void setIsnb(String isnb) {
        this.isnb = isnb;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getLocalizacaoFisica() {
        return localizacaoFisica;
    }

    public void setLocalizacaoFisica(String localizacaoFisica) {
        this.localizacaoFisica = localizacaoFisica;
    }

    public Set<Autor> getAutores() {
        return autores;
    }
    public void setAutores(Set<Autor> autores) {
        this.autores = autores;
    }

    public Set<Genero> getGeneros() {
        return generos;
    }
    public void setGeneros(Set<Genero> generos) {
        this.generos = generos;
    }

    public Estoque getEstoque() {
        return estoque;
    }
    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public Set<Foto> getFotos() {
        return fotos;
    }
    public void setFotos(Set<Foto> fotos) {
        this.fotos = fotos;
    }

    public Set<MovimentacaoEstoque> getMovimentacoes() {
        return movimentacoes;
    }
    public void setMovimentacoes(Set<MovimentacaoEstoque> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public Set<Reserva> getReservas() {
        return reservas;
    }
    public void setReservas(Set<Reserva> reservas) {
        this.reservas = reservas;
    }
}
