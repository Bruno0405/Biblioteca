package biblioteca.reservas.controller;

import biblioteca.estoque.data.Estoque;
import biblioteca.estoque.repository.RepositorioEstoque;
import biblioteca.multas.data.Multa;
import biblioteca.multas.repository.RepositorioMultas;
import biblioteca.reservas.data.Reserva;
import biblioteca.reservas.models.ReservaDTO;
import biblioteca.reservas.repository.RepositorioReservas;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaController {

    @Inject
    RepositorioReservas repositorioReservas;

    @Inject
    RepositorioEstoque repositorioEstoque;

    @Inject
    RepositorioMultas repositorioMultas;

    @GET
    public Response listarTodos(
            @QueryParam("idCliente") Integer idCliente,
            @QueryParam("idLivro") Integer idLivro,
            @QueryParam("status") String status) {

        List<Reserva> reservas;

        if (idCliente != null) {
            reservas = repositorioReservas.list("idCliente", idCliente);
        } else if (idLivro != null) {
            reservas = repositorioReservas.list("idLivro", idLivro);
        } else if (status != null) {
            reservas = repositorioReservas.list("statusReserva", status);
        } else {
            reservas = repositorioReservas.listAll();
        }

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
        Estoque estoque = repositorioEstoque.find("idLivro", reservaDTO.getIdLivro()).firstResult();

        if (estoque == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("O livro informado não possui estoque.")
                    .build();
        }

        int disponivel = estoque.getQuantidadeTotal()
                - estoque.getQuantidadeReservada()
                - estoque.getQuantidadeEmprestada()
                - estoque.getQuantidadeDanificada();

        if (disponivel <= 0) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Livro sem estoque disponível para reserva.")
                    .build();
        }

        estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() + 1);
        repositorioEstoque.persist(estoque);

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

        String statusAtual = reserva.getStatusReserva();
        String novoStatus = reservaDTO.getStatusReserva();

        // Retirada do livro: reservado -> emprestado
        if ("emprestado".equals(novoStatus) && "reservado".equals(statusAtual)) {
            Estoque estoque = repositorioEstoque.find("idLivro", reserva.getIdLivro()).firstResult();
            if (estoque != null) {
                estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - 1);
                estoque.setQuantidadeEmprestada(estoque.getQuantidadeEmprestada() + 1);
                repositorioEstoque.persist(estoque);
            }
        }

        // Devolução: emprestado -> devolvido
        if ("devolvido".equals(novoStatus) && "emprestado".equals(statusAtual)) {
            Estoque estoque = repositorioEstoque.find("idLivro", reserva.getIdLivro()).firstResult();
            if (estoque != null) {
                estoque.setQuantidadeEmprestada(estoque.getQuantidadeEmprestada() - 1);
                repositorioEstoque.persist(estoque);
            }
        }

        // Cancelamento: reservado -> cancelado
        if ("cancelado".equals(novoStatus) && "reservado".equals(statusAtual)) {
            Estoque estoque = repositorioEstoque.find("idLivro", reserva.getIdLivro()).firstResult();
            if (estoque != null) {
                estoque.setQuantidadeReservada(estoque.getQuantidadeReservada() - 1);
                repositorioEstoque.persist(estoque);
            }
        }

        // Atraso: emprestado -> atrasado, gera multa automaticamente
        if ("atrasado".equals(novoStatus) && "emprestado".equals(statusAtual)) {
            // Verifica se já existe multa para essa reserva
            Multa multaExistente = repositorioMultas.find("idReserva", reserva.getIdReserva()).firstResult();

            if (multaExistente == null) {
                // Calcula dias de atraso
                LocalDate hoje = LocalDate.now();
                LocalDate dataPrevista = reserva.getDataPrevistaDevolucao();
                long diasAtraso = dataPrevista != null ? ChronoUnit.DAYS.between(dataPrevista, hoje) : 1;
                if (diasAtraso < 1) diasAtraso = 1;

                // R$ 2,00 por dia de atraso, ver de ajustar depois se necessário
                BigDecimal valorMulta = BigDecimal.valueOf(2.00).multiply(BigDecimal.valueOf(diasAtraso));

                Multa multa = new Multa();
                multa.setIdReserva(reserva.getIdReserva());
                multa.setValorMulta(valorMulta);
                multa.setDataMulta(hoje);
                multa.setStatusMulta("pendente");
                repositorioMultas.persist(multa);
            }
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