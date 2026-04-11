package biblioteca.funcionarios.repository;

import biblioteca.funcionarios.data.Funcionario;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioFuncionarios implements PanacheRepositoryBase<Funcionario, Integer> {}