package biblioteca.autores.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Autores")
public class Autor extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autor")
    private Integer idAutor;

    @Column(name = "nome_autor")
    private String nomeAutor;

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
}