package biblioteca.autores.controller;

import biblioteca.autores.data.Autor;
import biblioteca.autores.models.AutorDTO;
import biblioteca.autores.repository.RepositorioAutores;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/autores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutorController {

    @Inject
    RepositorioAutores repositorioAutores;

    @GET
    public Response listarTodos() {
        List<Autor> autores = repositorioAutores.listAll();
        List<AutorDTO> autoresDTO = autores.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(autoresDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Autor autor = repositorioAutores.findById(id);
        if (autor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(autor)).build();
    }

    @POST
    @Transactional
    public Response criar(AutorDTO autorDTO) {
        Autor autor = transformeEmEntidade(autorDTO);
        repositorioAutores.persist(autor);
        return Response.status(Response.Status.CREATED)
                .entity(transformeEmDto(autor))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, AutorDTO autorDTO) {
        Autor autor = repositorioAutores.findById(id);
        if (autor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        autor.setNomeAutor(autorDTO.getNomeAutor());
        repositorioAutores.persist(autor);
        return Response.ok(transformeEmDto(autor)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioAutores.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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
}