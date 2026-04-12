package biblioteca.logs.repository;

import biblioteca.logs.data.Log;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioLogs implements PanacheRepositoryBase<Log, Integer> {}