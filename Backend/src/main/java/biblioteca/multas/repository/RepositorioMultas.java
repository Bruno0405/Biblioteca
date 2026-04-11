package biblioteca.multas.repository;

import biblioteca.multas.data.Multa;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioMultas implements PanacheRepositoryBase<Multa, Integer> {}