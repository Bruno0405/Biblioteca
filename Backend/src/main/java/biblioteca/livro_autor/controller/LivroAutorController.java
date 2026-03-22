package biblioteca.livro_autor.controller;

import biblioteca.livro_autor.data.LivroAutor;
import biblioteca.livro_autor.data.LivroAutorId;
import biblioteca.livro_autor.models.LivroAutorDTO;
import biblioteca.livro_autor.repository.RepositorioLivroAutor;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/livro-autor")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivroAutorController {

    @Inject
    RepositorioLivroAutor repositorioLivroAutor;

    @GET
    public Response listarTodos() {
        List<LivroAutor> lista = repositorioLivroAutor.listAll();
        List<LivroAutorDTO> listaDTO = lista.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(listaDTO).build();
    }

    @GET
    @Path("/{idLivro}/{idAutor}")
    public Response buscarPorId(@PathParam("idLivro") Integer idLivro,
                                @PathParam("idAutor") Integer idAutor) {
        LivroAutorId chave = new LivroAutorId(idLivro, idAutor);
        LivroAutor livroAutor = repositorioLivroAutor.findById(chave);
        if (livroAutor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(livroAutor)).build();
    }

    @POST
    @Transactional
    public Response criar(LivroAutorDTO dto) {
        LivroAutor livroAutor = transformeEmEntidade(dto);
        repositorioLivroAutor.persist(livroAutor);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(livroAutor))
                .build();
    }

    @DELETE
    @Path("/{idLivro}/{idAutor}")
    @Transactional
    public Response deletar(@PathParam("idLivro") Integer idLivro,
                            @PathParam("idAutor") Integer idAutor) {
        LivroAutorId chave = new LivroAutorId(idLivro, idAutor);
        boolean deletado = repositorioLivroAutor.deleteById(chave);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private LivroAutorDTO transformeEmDto(LivroAutor livroAutor) {
        LivroAutorDTO dto = new LivroAutorDTO();
        dto.setIdLivro(livroAutor.getId().getIdLivro());
        dto.setIdAutor(livroAutor.getId().getIdAutor());
        return dto;
    }

    private LivroAutor transformeEmEntidade(LivroAutorDTO dto) {
        LivroAutor livroAutor = new LivroAutor();
        livroAutor.setId(new LivroAutorId(dto.getIdLivro(), dto.getIdAutor()));
        return livroAutor;
    }
}