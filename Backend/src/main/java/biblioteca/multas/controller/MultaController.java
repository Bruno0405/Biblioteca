package biblioteca.multas.controller;

import biblioteca.multas.data.Multa;
import biblioteca.multas.models.MultaDTO;
import biblioteca.multas.repository.RepositorioMultas;
import biblioteca.reservas.data.Reserva;
import biblioteca.reservas.repository.RepositorioReservas;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/multas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MultaController {

    @Inject
    RepositorioMultas repositorioMultas;

    @Inject
    RepositorioReservas repositorioReservas;

    @Inject
    LogService logService;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"cliente", "funcionario", "gerente", "admin"})
    public Response listarTodos(
            @QueryParam("idReserva") Integer idReserva,
            @QueryParam("status") String status) {

        String userType = jwt.getClaim("userType");
        List<Multa> multas;

        if ("cliente".equals(userType)) {
            Integer userId = Integer.valueOf(jwt.getClaim("userId").toString());
            List<Reserva> reservas = repositorioReservas.list("idCliente", userId);
            List<Integer> idsReservas = reservas.stream()
                    .map(Reserva::getIdReserva)
                    .collect(Collectors.toList());
            if (idsReservas.isEmpty()) {
                return Response.ok(List.of()).build();
            }
            multas = repositorioMultas.list("idReserva IN ?1", idsReservas);
        } else if (idReserva != null) {
            multas = repositorioMultas.list("idReserva", idReserva);
        } else if (status != null) {
            multas = repositorioMultas.list("statusMulta", status);
        } else {
            multas = repositorioMultas.listAll();
        }

        List<MultaDTO> multasDTO = multas.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(multasDTO).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"cliente", "funcionario", "gerente", "admin"})
    public Response buscarPorId(@PathParam("id") Integer id) {
        Multa multa = repositorioMultas.findById(id);
        if (multa == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(multa)).build();
    }

    @POST
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response criar(MultaDTO multaDTO) {
        Multa multa = transformeEmEntidade(multaDTO);
        repositorioMultas.persist(multa);

        Log logEntry = new Log();
        logEntry.setAcao("Criou multa");
        logService.log(logEntry);

        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(multa))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"gerente", "admin"})
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

        Log logEntry = new Log();
        logEntry.setAcao("Atualizou multa (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(transformeEmDto(multa)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioMultas.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu multa (id: " + id + ")");
        logService.log(logEntry);

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