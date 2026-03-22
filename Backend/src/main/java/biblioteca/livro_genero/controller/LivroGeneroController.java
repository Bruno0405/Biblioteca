package biblioteca.livro_genero.controller;

import biblioteca.livro_genero.data.LivroGenero;
import biblioteca.livro_genero.data.LivroGeneroId;
import biblioteca.livro_genero.models.LivroGeneroDTO;
import biblioteca.livro_genero.repository.RepositorioLivroGenero;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/livro-genero")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivroGeneroController {

    @Inject
    RepositorioLivroGenero repositorioLivroGenero;

    @GET
    public Response listarTodos() {
        List<LivroGenero> lista = repositorioLivroGenero.listAll();
        List<LivroGeneroDTO> listaDTO = lista.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(listaDTO).build();
    }

    @GET
    @Path("/{idLivro}/{idGenero}")
    public Response buscarPorId(@PathParam("idLivro") Integer idLivro,
                                @PathParam("idGenero") Integer idGenero) {
        LivroGeneroId chave = new LivroGeneroId(idLivro, idGenero);
        LivroGenero livroGenero = repositorioLivroGenero.findById(chave);
        if (livroGenero == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(livroGenero)).build();
    }

    @POST
    @Transactional
    public Response criar(LivroGeneroDTO dto) {
        LivroGenero livroGenero = transformeEmEntidade(dto);
        repositorioLivroGenero.persist(livroGenero);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(livroGenero))
                .build();
    }

    @DELETE
    @Path("/{idLivro}/{idGenero}")
    @Transactional
    public Response deletar(@PathParam("idLivro") Integer idLivro,
                            @PathParam("idGenero") Integer idGenero) {
        LivroGeneroId chave = new LivroGeneroId(idLivro, idGenero);
        boolean deletado = repositorioLivroGenero.deleteById(chave);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private LivroGeneroDTO transformeEmDto(LivroGenero livroGenero) {
        LivroGeneroDTO dto = new LivroGeneroDTO();
        dto.setIdLivro(livroGenero.getId().getIdLivro());
        dto.setIdGenero(livroGenero.getId().getIdGenero());
        return dto;
    }

    private LivroGenero transformeEmEntidade(LivroGeneroDTO dto) {
        LivroGenero livroGenero = new LivroGenero();
        livroGenero.setId(new LivroGeneroId(dto.getIdLivro(), dto.getIdGenero()));
        return livroGenero;
    }
}