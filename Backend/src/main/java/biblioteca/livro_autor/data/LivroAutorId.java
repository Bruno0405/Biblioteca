package biblioteca.livro_autor.data;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LivroAutorId implements Serializable {

    private Integer idLivro;
    private Integer idAutor;

    public LivroAutorId() {}

    public LivroAutorId(Integer idLivro, Integer idAutor) {
        this.idLivro = idLivro;
        this.idAutor = idAutor;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Integer getIdAutor() {
        return idAutor;
    }
    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LivroAutorId)) return false;
        LivroAutorId that = (LivroAutorId) o;
        return Objects.equals(idLivro, that.idLivro) && Objects.equals(idAutor, that.idAutor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivro, idAutor);
    }
}