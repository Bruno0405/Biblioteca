package biblioteca.generos.controller;

import biblioteca.generos.data.Genero;
import biblioteca.generos.models.GeneroDTO;
import biblioteca.generos.repository.RepositorioGeneros;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/generos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeneroController {

    @Inject
    RepositorioGeneros repositorioGeneros;

    @GET
    public Response listarTodos() {
        List<Genero> generos = repositorioGeneros.listAll();
        List<GeneroDTO> generosDTO = generos.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(generosDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Genero genero = repositorioGeneros.findById(id);
        if (genero == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(genero)).build();
    }

    @POST
    @Transactional
    public Response criar(GeneroDTO generoDTO) {
        Genero genero = transformeEmEntidade(generoDTO);
        repositorioGeneros.persist(genero);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(genero))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, GeneroDTO generoDTO) {
        Genero genero = repositorioGeneros.findById(id);
        if (genero == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        genero.setNomeGenero(generoDTO.getNomeGenero());
        repositorioGeneros.persist(genero);
        return Response.ok(transformeEmDto(genero)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioGeneros.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private GeneroDTO transformeEmDto(Genero genero) {
        GeneroDTO dto = new GeneroDTO();
        dto.setIdGenero(genero.getIdGenero());
        dto.setNomeGenero(genero.getNomeGenero());
        return dto;
    }

    private Genero transformeEmEntidade(GeneroDTO dto) {
        Genero genero = new Genero();
        genero.setNomeGenero(dto.getNomeGenero());
        return genero;
    }
}