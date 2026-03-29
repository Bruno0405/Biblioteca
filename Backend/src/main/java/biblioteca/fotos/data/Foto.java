package biblioteca.fotos.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "fotos")
public class Foto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Integer idFoto;

    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "foto")
    private String foto;

    public Integer getIdFoto() {
        return idFoto;
    }
    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }
}
