package biblioteca.clientes.repository;

import biblioteca.clientes.data.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioClientes implements PanacheRepositoryBase<Cliente, Integer> {}