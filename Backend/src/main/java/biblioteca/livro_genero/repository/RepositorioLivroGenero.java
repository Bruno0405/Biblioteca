package biblioteca.livro_genero.repository;

import biblioteca.livro_genero.data.LivroGenero;
import biblioteca.livro_genero.data.LivroGeneroId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioLivroGenero implements PanacheRepositoryBase<LivroGenero, LivroGeneroId> {}