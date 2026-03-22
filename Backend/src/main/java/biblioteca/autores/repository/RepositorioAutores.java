package biblioteca.autores.repository;

import biblioteca.autores.data.Autor;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioAutores implements PanacheRepositoryBase<Autor, Integer> {}