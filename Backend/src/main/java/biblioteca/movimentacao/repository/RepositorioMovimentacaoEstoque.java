package biblioteca.movimentacao.repository;

import biblioteca.movimentacao.data.MovimentacaoEstoque;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioMovimentacaoEstoque implements PanacheRepositoryBase<MovimentacaoEstoque, Integer> {}