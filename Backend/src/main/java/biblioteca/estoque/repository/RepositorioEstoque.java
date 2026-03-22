package biblioteca.estoque.repository;

import biblioteca.estoque.data.Estoque;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioEstoque implements PanacheRepositoryBase<Estoque, Integer> {}