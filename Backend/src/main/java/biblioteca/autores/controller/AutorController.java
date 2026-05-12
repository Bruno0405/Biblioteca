package biblioteca.autores.controller;

import biblioteca.autores.data.Autor;
import biblioteca.autores.models.AutorDTO;
import biblioteca.autores.repository.RepositorioAutores;
import biblioteca.livros.models.LivroDTO;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/autores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AutorController {

    @Inject
    RepositorioAutores repositorioAutores;

    @Inject
    LogService logService;

    @GET
    @PermitAll
    public Response listarTodos() {
        List<Autor> autores = repositorioAutores.listAll();
        List<AutorDTO> autoresDTO = autores.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(autoresDTO).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response buscarPorId(@PathParam("id") Integer id) {
        Autor autor = repositorioAutores.findById(id);
        if (autor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(autor)).build();
    }

    @POST
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response criar(AutorDTO autorDTO) {
        Autor autor = transformeEmEntidade(autorDTO);
        repositorioAutores.persist(autor);

        Log logEntry = new Log();
        logEntry.setAcao("Criou autor");
        logService.log(logEntry);

        return Response.status(Response.Status.CREATED)
                .entity(transformeEmDto(autor))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, AutorDTO autorDTO) {
        Autor autor = repositorioAutores.findById(id);
        if (autor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        autor.setNomeAutor(autorDTO.getNomeAutor());
        repositorioAutores.persist(autor);

        Log logEntry = new Log();
        logEntry.setAcao("Atualizou autor (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(transformeEmDto(autor)).build();
    }

    @GET
    @Path("/{id}/livros")
    @PermitAll
    public Response listarLivros(@PathParam("id") Integer id) {
        Autor autor = repositorioAutores.findById(id);
        if (autor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<LivroDTO> livros = autor.getLivros().stream()
                .map(this::toLivroDtoResumo)
                .toList();
        return Response.ok(livros).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioAutores.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu autor (id: " + id + ")");
        logService.log(logEntry);

        return Response.noContent().build();
    }

    private AutorDTO transformeEmDto(Autor autor) {
        AutorDTO dto = new AutorDTO();
        dto.setIdAutor(autor.getIdAutor());
        dto.setNomeAutor(autor.getNomeAutor());
        return dto;
    }

    private Autor transformeEmEntidade(AutorDTO dto) {
        Autor autor = new Autor();
        autor.setNomeAutor(dto.getNomeAutor());
        return autor;
    }

    private LivroDTO toLivroDtoResumo(biblioteca.livros.data.Livro livro) {
        LivroDTO ld = new LivroDTO();
        ld.setIdLivro(livro.getIdLivro());
        ld.setNomeLivro(livro.getNomeLivro());
        return ld;
    }
}
