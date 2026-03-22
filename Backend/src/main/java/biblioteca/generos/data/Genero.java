package biblioteca.generos.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Generos")
public class Genero extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Integer idGenero;

    @Column(name = "nome_genero")
    private String nomeGenero;

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
}