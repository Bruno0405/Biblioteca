package biblioteca.autores.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import biblioteca.livros.data.Livro;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Autores")
public class Autor extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autor")
    private Integer idAutor;

    @Column(name = "nome_autor")
    private String nomeAutor;

    @ManyToMany(mappedBy = "autores", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("autores")
    private Set<Livro> livros = new HashSet<>();

    public Integer getIdAutor() {
        return idAutor;
    }
    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor; }

    public String getNomeAutor() {
        return nomeAutor;
    }
    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public Set<Livro> getLivros() {
        return livros;
    }
    public void setLivros(Set<Livro> livros) {
        this.livros = livros;
    }
}