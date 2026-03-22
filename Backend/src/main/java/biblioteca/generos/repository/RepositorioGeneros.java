package biblioteca.generos.repository;

import biblioteca.generos.data.Genero;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioGeneros implements PanacheRepositoryBase<Genero, Integer> {}