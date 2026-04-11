package biblioteca.historico_cliente.repository;

import biblioteca.historico_cliente.data.HistoricoCliente;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioHistoricoCliente implements PanacheRepositoryBase<HistoricoCliente, Integer> {}