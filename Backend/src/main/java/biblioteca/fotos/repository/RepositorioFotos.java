package biblioteca.fotos.repository;

import biblioteca.fotos.data.Foto;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RepositorioFotos implements PanacheRepositoryBase<Foto, Integer> {}