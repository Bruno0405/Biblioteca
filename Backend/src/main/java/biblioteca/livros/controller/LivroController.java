package biblioteca.livros.controller;

import biblioteca.autores.data.Autor;
import biblioteca.autores.models.AutorDTO;
import biblioteca.autores.repository.RepositorioAutores;
import biblioteca.generos.data.Genero;
import biblioteca.generos.models.GeneroDTO;
import biblioteca.generos.repository.RepositorioGeneros;
import biblioteca.livros.data.Livro;
import biblioteca.livros.models.LivroDTO;
import biblioteca.livros.repository.RepositorioLivros;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/livros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class LivroController {

    @Inject
    RepositorioLivros repositorioLivros;

    @Inject
    RepositorioAutores repositorioAutores;

    @Inject
    RepositorioGeneros repositorioGeneros;

    @Inject
    LogService logService;

    @GET
    @PermitAll
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
    @PermitAll
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
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response criar(LivroDTO livroDTO) {
        Livro livro = transformeEmEntidade(livroDTO);

        if (livroDTO.getIdAutores() != null && !livroDTO.getIdAutores().isEmpty()) {
            List<Autor> autores = repositorioAutores.list("idAutor IN ?1", livroDTO.getIdAutores());
            livro.setAutores(new HashSet<>(autores));
        }

        if (livroDTO.getIdGeneros() != null && !livroDTO.getIdGeneros().isEmpty()) {
            List<Genero> generos = repositorioGeneros.list("idGenero IN ?1", livroDTO.getIdGeneros());
            livro.setGeneros(new HashSet<>(generos));
        }

        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Criou livro");
        logService.log(logEntry);

        return Response
                .status(Response.Status.CREATED)
                .entity(tranformeEmDto(livro))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"gerente", "admin"})
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

        if (livroDTO.getIdAutores() != null) {
            List<Autor> autores = repositorioAutores.list("idAutor IN ?1", livroDTO.getIdAutores());
            livro.setAutores(new HashSet<>(autores));
        }

        if (livroDTO.getIdGeneros() != null) {
            List<Genero> generos = repositorioGeneros.list("idGenero IN ?1", livroDTO.getIdGeneros());
            livro.setGeneros(new HashSet<>(generos));
        }

        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Atualizou livro (id: " + id + ")");
        logService.log(logEntry);

        return Response
                .ok(tranformeEmDto(livro))
                .build();
    }

    @GET
    @Path("/{id}/autores")
    @PermitAll
    public Response listarAutores(@PathParam("id") Integer id) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<AutorDTO> autores = livro.getAutores().stream()
                .map(this::toAutorDto)
                .toList();
        return Response.ok(autores).build();
    }

    @POST
    @Path("/{id}/autores")
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response adicionarAutores(@PathParam("id") Integer id, Set<Integer> idAutores) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Autor> autores = repositorioAutores.list("idAutor IN ?1", idAutores);
        livro.getAutores().addAll(autores);
        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Adicionou autor(es) ao livro (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(tranformeEmDto(livro)).build();
    }

    @DELETE
    @Path("/{id}/autores/{idAutor}")
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response removerAutor(@PathParam("id") Integer id, @PathParam("idAutor") Integer idAutor) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        livro.getAutores().removeIf(a -> a.getIdAutor().equals(idAutor));
        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Removeu autor do livro (livro id: " + id + ", autor id: " + idAutor + ")");
        logService.log(logEntry);

        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/generos")
    @PermitAll
    public Response listarGeneros(@PathParam("id") Integer id) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<GeneroDTO> generos = livro.getGeneros().stream()
                .map(this::toGeneroDto)
                .toList();
        return Response.ok(generos).build();
    }

    @POST
    @Path("/{id}/generos")
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response adicionarGeneros(@PathParam("id") Integer id, Set<Integer> idGeneros) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Genero> generos = repositorioGeneros.list("idGenero IN ?1", idGeneros);
        livro.getGeneros().addAll(generos);
        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Adicionou genero(s) ao livro (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(tranformeEmDto(livro)).build();
    }

    @DELETE
    @Path("/{id}/generos/{idGenero}")
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response removerGenero(@PathParam("id") Integer id, @PathParam("idGenero") Integer idGenero) {
        Livro livro = repositorioLivros.findById(id);
        if (livro == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        livro.getGeneros().removeIf(g -> g.getIdGenero().equals(idGenero));
        repositorioLivros.persist(livro);

        Log logEntry = new Log();
        logEntry.setAcao("Removeu genero do livro (livro id: " + id + ", genero id: " + idGenero + ")");
        logService.log(logEntry);

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioLivros.deleteById(id);

        if (!deletado) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu livro (id: " + id + ")");
        logService.log(logEntry);

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

        if (livro.getAutores() != null) {
            dto.setAutores(livro.getAutores().stream()
                    .map(this::toAutorDto)
                    .toList());
        } else {
            dto.setAutores(Collections.emptyList());
        }

        if (livro.getGeneros() != null) {
            dto.setGeneros(livro.getGeneros().stream()
                    .map(this::toGeneroDto)
                    .toList());
        } else {
            dto.setGeneros(Collections.emptyList());
        }

        return dto;
    }

    private AutorDTO toAutorDto(Autor autor) {
        AutorDTO ad = new AutorDTO();
        ad.setIdAutor(autor.getIdAutor());
        ad.setNomeAutor(autor.getNomeAutor());
        return ad;
    }

    private GeneroDTO toGeneroDto(Genero genero) {
        GeneroDTO gd = new GeneroDTO();
        gd.setIdGenero(genero.getIdGenero());
        gd.setNomeGenero(genero.getNomeGenero());
        return gd;
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
