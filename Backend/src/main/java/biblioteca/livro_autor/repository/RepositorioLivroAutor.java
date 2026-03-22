package biblioteca.livro_autor.repository;

import biblioteca.livro_autor.data.LivroAutor;
import biblioteca.livro_autor.data.LivroAutorId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioLivroAutor implements PanacheRepositoryBase<LivroAutor, LivroAutorId> {}