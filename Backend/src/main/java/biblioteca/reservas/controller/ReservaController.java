package biblioteca.reservas.controller;

import biblioteca.reservas.data.Reserva;
import biblioteca.reservas.models.ReservaDTO;
import biblioteca.reservas.repository.RepositorioReservas;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaController {

    @Inject
    RepositorioReservas repositorioReservas;

    @GET
    public Response listarTodos() {
        List<Reserva> reservas = repositorioReservas.listAll();
        List<ReservaDTO> reservasDTO = reservas.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(reservasDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Reserva reserva = repositorioReservas.findById(id);
        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(reserva)).build();
    }

    @POST
    @Transactional
    public Response criar(ReservaDTO reservaDTO) {
        Reserva reserva = transformeEmEntidade(reservaDTO);
        repositorioReservas.persist(reserva);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(reserva))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, ReservaDTO reservaDTO) {
        Reserva reserva = repositorioReservas.findById(id);
        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reserva.setIdCliente(reservaDTO.getIdCliente());
        reserva.setIdLivro(reservaDTO.getIdLivro());
        reserva.setIdFuncionarioRetirada(reservaDTO.getIdFuncionarioRetirada());
        reserva.setIdFuncionarioDevolucao(reservaDTO.getIdFuncionarioDevolucao());
        reserva.setDataReserva(reservaDTO.getDataReserva());
        reserva.setDataLimiteRetirada(reservaDTO.getDataLimiteRetirada());
        reserva.setDataRetirada(reservaDTO.getDataRetirada());
        reserva.setDataPrevistaDevolucao(reservaDTO.getDataPrevistaDevolucao());
        reserva.setDataDevolucao(reservaDTO.getDataDevolucao());
        reserva.setStatusReserva(reservaDTO.getStatusReserva());
        reserva.setCodigoReserva(reservaDTO.getCodigoReserva());
        repositorioReservas.persist(reserva);
        return Response.ok(transformeEmDto(reserva)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioReservas.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private ReservaDTO transformeEmDto(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setIdCliente(reserva.getIdCliente());
        dto.setIdLivro(reserva.getIdLivro());
        dto.setIdFuncionarioRetirada(reserva.getIdFuncionarioRetirada());
        dto.setIdFuncionarioDevolucao(reserva.getIdFuncionarioDevolucao());
        dto.setDataReserva(reserva.getDataReserva());
        dto.setDataLimiteRetirada(reserva.getDataLimiteRetirada());
        dto.setDataRetirada(reserva.getDataRetirada());
        dto.setDataPrevistaDevolucao(reserva.getDataPrevistaDevolucao());
        dto.setDataDevolucao(reserva.getDataDevolucao());
        dto.setStatusReserva(reserva.getStatusReserva());
        dto.setCodigoReserva(reserva.getCodigoReserva());
        return dto;
    }

    private Reserva transformeEmEntidade(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setIdCliente(dto.getIdCliente());
        reserva.setIdLivro(dto.getIdLivro());
        reserva.setIdFuncionarioRetirada(dto.getIdFuncionarioRetirada());
        reserva.setIdFuncionarioDevolucao(dto.getIdFuncionarioDevolucao());
        reserva.setDataReserva(dto.getDataReserva());
        reserva.setDataLimiteRetirada(dto.getDataLimiteRetirada());
        reserva.setDataRetirada(dto.getDataRetirada());
        reserva.setDataPrevistaDevolucao(dto.getDataPrevistaDevolucao());
        reserva.setDataDevolucao(dto.getDataDevolucao());
        reserva.setStatusReserva(dto.getStatusReserva() != null ? dto.getStatusReserva() : "reservado");
        reserva.setCodigoReserva(dto.getCodigoReserva());
        return reserva;
    }
}