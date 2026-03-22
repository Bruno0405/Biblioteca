package biblioteca.livro_genero.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Genero_livro")
public class LivroGenero extends PanacheEntityBase {

    @EmbeddedId
    private LivroGeneroId id;

    public LivroGeneroId getId() { return id; }
    public void setId(LivroGeneroId id) { this.id = id; }
}