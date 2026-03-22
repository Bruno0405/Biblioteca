package biblioteca.livro_autor.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Livro_Autor")
public class LivroAutor extends PanacheEntityBase {

    @EmbeddedId
    private LivroAutorId id;

    public LivroAutorId getId() {
        return id;
    }
    public void setId(LivroAutorId id) {
        this.id = id;
    }
}