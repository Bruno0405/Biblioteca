package biblioteca.multas.controller;

import biblioteca.multas.data.Multa;
import biblioteca.multas.models.MultaDTO;
import biblioteca.multas.repository.RepositorioMultas;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/multas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MultaController {

    @Inject
    RepositorioMultas repositorioMultas;

    @GET
    public Response listarTodos() {
        List<Multa> multas = repositorioMultas.listAll();
        List<MultaDTO> multasDTO = multas.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(multasDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Multa multa = repositorioMultas.findById(id);
        if (multa == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(multa)).build();
    }

    @POST
    @Transactional
    public Response criar(MultaDTO multaDTO) {
        Multa multa = transformeEmEntidade(multaDTO);
        repositorioMultas.persist(multa);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(multa))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, MultaDTO multaDTO) {
        Multa multa = repositorioMultas.findById(id);
        if (multa == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        multa.setIdReserva(multaDTO.getIdReserva());
        multa.setValorMulta(multaDTO.getValorMulta());
        multa.setDataMulta(multaDTO.getDataMulta());
        multa.setStatusMulta(multaDTO.getStatusMulta());
        multa.setDataPagamento(multaDTO.getDataPagamento());
        repositorioMultas.persist(multa);
        return Response.ok(transformeEmDto(multa)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioMultas.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private MultaDTO transformeEmDto(Multa multa) {
        MultaDTO dto = new MultaDTO();
        dto.setIdMulta(multa.getIdMulta());
        dto.setIdReserva(multa.getIdReserva());
        dto.setValorMulta(multa.getValorMulta());
        dto.setDataMulta(multa.getDataMulta());
        dto.setStatusMulta(multa.getStatusMulta());
        dto.setDataPagamento(multa.getDataPagamento());
        return dto;
    }

    private Multa transformeEmEntidade(MultaDTO dto) {
        Multa multa = new Multa();
        multa.setIdReserva(dto.getIdReserva());
        multa.setValorMulta(dto.getValorMulta());
        multa.setDataMulta(dto.getDataMulta());
        multa.setStatusMulta(dto.getStatusMulta());
        multa.setDataPagamento(dto.getDataPagamento());
        return multa;
    }
}