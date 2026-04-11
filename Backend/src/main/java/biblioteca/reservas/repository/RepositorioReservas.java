package biblioteca.reservas.repository;

import biblioteca.reservas.data.Reserva;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioReservas implements PanacheRepositoryBase<Reserva, Integer> {}