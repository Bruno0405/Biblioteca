package biblioteca.livros.repository;

import biblioteca.livros.data.Livro;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioLivros implements PanacheRepositoryBase<Livro, Integer> {}
