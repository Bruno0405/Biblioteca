package biblioteca.generos.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import biblioteca.livros.data.Livro;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Generos")
public class Genero extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Integer idGenero;

    @Column(name = "nome_genero")
    private String nomeGenero;

    @ManyToMany(mappedBy = "generos", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("generos")
    private Set<Livro> livros = new HashSet<>();

    public Integer getIdGenero() {
        return idGenero;
    }
    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNomeGenero() {
        return nomeGenero;
    }
    public void setNomeGenero(String nomeGenero) {
        this.nomeGenero = nomeGenero;
    }

    public Set<Livro> getLivros() {
        return livros;
    }
    public void setLivros(Set<Livro> livros) {
        this.livros = livros;
    }
}