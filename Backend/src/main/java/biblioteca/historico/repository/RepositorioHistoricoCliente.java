package biblioteca.historico.repository;

import biblioteca.historico.data.HistoricoCliente;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioHistoricoCliente implements PanacheRepositoryBase<HistoricoCliente, Integer> {}