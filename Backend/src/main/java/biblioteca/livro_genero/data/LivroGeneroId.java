package biblioteca.livro_genero.data;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LivroGeneroId implements Serializable {

    private Integer idLivro;
    private Integer idGenero;

    public LivroGeneroId() {}

    public LivroGeneroId(Integer idLivro, Integer idGenero) {
        this.idLivro = idLivro;
        this.idGenero = idGenero;
    }

    public Integer getIdLivro() { return idLivro; }
    public void setIdLivro(Integer idLivro) { this.idLivro = idLivro; }

    public Integer getIdGenero() { return idGenero; }
    public void setIdGenero(Integer idGenero) { this.idGenero = idGenero; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LivroGeneroId)) return false;
        LivroGeneroId that = (LivroGeneroId) o;
        return Objects.equals(idLivro, that.idLivro) && Objects.equals(idGenero, that.idGenero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivro, idGenero);
    }
}