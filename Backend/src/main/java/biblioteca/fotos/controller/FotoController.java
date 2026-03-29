package biblioteca.fotos.controller;

import biblioteca.fotos.data.Foto;
import biblioteca.fotos.models.FotoDTO;
import biblioteca.fotos.repository.RepositorioFotos;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/fotos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FotoController {

    @Inject
    RepositorioFotos repositorioFotos;

    @GET
    public Response listarTodos() {
        List<Foto> fotos = repositorioFotos.listAll();
        List<FotoDTO> fotosDTO = fotos.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(fotosDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Foto foto = repositorioFotos.findById(id);
        if (foto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(foto)).build();
    }

    @POST
    @Transactional
    public Response criar(FotoDTO fotoDTO) {
        Foto foto = transformeEmEntidade(fotoDTO);
        repositorioFotos.persist(foto);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(foto))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, FotoDTO fotoDTO) {
        Foto foto = repositorioFotos.findById(id);
        if (foto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        foto.setIdLivro(fotoDTO.getIdLivro());
        foto.setFoto(fotoDTO.getFoto());
        repositorioFotos.persist(foto);
        return Response.ok(transformeEmDto(foto)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioFotos.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private FotoDTO transformeEmDto(Foto foto) {
        FotoDTO dto = new FotoDTO();
        dto.setIdFoto(foto.getIdFoto());
        dto.setIdLivro(foto.getIdLivro());
        dto.setFoto(foto.getFoto());
        return dto;
    }

    private Foto transformeEmEntidade(FotoDTO dto) {
        Foto foto = new Foto();
        foto.setIdLivro(dto.getIdLivro());
        foto.setFoto(dto.getFoto());
        return foto;
    }
}