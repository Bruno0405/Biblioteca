package biblioteca.livros.controller;

import biblioteca.livros.data.Livro;
import biblioteca.livros.models.LivroDTO;
import biblioteca.livros.repository.RepositorioLivros;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/livros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivroController {

    @Inject
    RepositorioLivros repositorioLivros;

    @GET
    public Response listarTodos(@QueryParam("nome") String nome,
                                @QueryParam("editora") String editora,
                                @QueryParam("ano") Integer ano) {
        List<Livro> livros;

        // Filtra por ano, editora ou nome, se os parâmetros estiverem vazios, lista tudo
        if (nome != null) {
            livros = repositorioLivros.list("LOWER(nomeLivro) LIKE LOWER(?1)", "%" + nome + "%");
        } else if (editora != null) {
            livros = repositorioLivros.list("LOWER(editora) LIKE LOWER(?1)", "%" + editora + "%");
        } else if (ano != null) {
            livros = repositorioLivros.list("ano", ano);
        } else {
            livros = repositorioLivros.listAll();
        }

        List<LivroDTO> livrosDTO = livros.stream()
                .map(this::tranformeEmDto)
                .toList();

        return Response
                .ok(livrosDTO)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Livro livro = repositorioLivros.findById(id);

        if (livro == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .ok(tranformeEmDto(livro))
                .build();
    }

    @POST
    @Transactional
    public Response criar(LivroDTO livroDTO) {
        Livro livro = transformeEmEntidade(livroDTO);

        repositorioLivros.persist(livro);

        return Response
                .status(Response.Status.CREATED)
                .entity(tranformeEmDto(livro))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, LivroDTO livroDTO) {
        Livro livro = repositorioLivros.findById(id);

        if (livro == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        livro.setNomeLivro(livroDTO.getNomeLivro());
        livro.setIsnb(livroDTO.getIsnb());
        livro.setEditora(livroDTO.getEditora());
        livro.setAno(livroDTO.getAno());
        livro.setSinopse(livroDTO.getSinopse());
        livro.setLocalizacaoFisica(livroDTO.getLocalizacaoFisica());

        repositorioLivros.persist(livro);

        return Response
                .ok(tranformeEmDto(livro))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioLivros.deleteById(id);

        if (!deletado) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    private LivroDTO tranformeEmDto(Livro livro) {
        LivroDTO dto = new LivroDTO();
        dto.setIdLivro(livro.getIdLivro());
        dto.setNomeLivro(livro.getNomeLivro());
        dto.setIsnb(livro.getIsnb());
        dto.setEditora(livro.getEditora());
        dto.setAno(livro.getAno());
        dto.setSinopse(livro.getSinopse());
        dto.setLocalizacaoFisica(livro.getLocalizacaoFisica());
        return dto;
    }

    private Livro transformeEmEntidade(LivroDTO dto) {
        Livro livro = new Livro();
        livro.setNomeLivro(dto.getNomeLivro());
        livro.setIsnb(dto.getIsnb());
        livro.setEditora(dto.getEditora());
        livro.setAno(dto.getAno());
        livro.setSinopse(dto.getSinopse());
        livro.setLocalizacaoFisica(dto.getLocalizacaoFisica());
        return livro;
    }
}
