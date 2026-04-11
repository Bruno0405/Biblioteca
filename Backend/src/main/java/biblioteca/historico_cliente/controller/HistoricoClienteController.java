package biblioteca.historico_cliente.controller;

import biblioteca.historico_cliente.data.HistoricoCliente;
import biblioteca.historico_cliente.models.HistoricoClienteDTO;
import biblioteca.historico_cliente.repository.RepositorioHistoricoCliente;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/historico-cliente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoricoClienteController {

    @Inject
    RepositorioHistoricoCliente repositorioHistoricoCliente;

    @GET
    public Response listarTodos() {
        List<HistoricoCliente> historicos = repositorioHistoricoCliente.listAll();
        List<HistoricoClienteDTO> historicosDTO = historicos.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(historicosDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        HistoricoCliente historico = repositorioHistoricoCliente.findById(id);
        if (historico == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(historico)).build();
    }

    @POST
    @Transactional
    public Response criar(HistoricoClienteDTO historicoDTO) {
        HistoricoCliente historico = transformeEmEntidade(historicoDTO);
        repositorioHistoricoCliente.persist(historico);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(historico))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioHistoricoCliente.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private HistoricoClienteDTO transformeEmDto(HistoricoCliente historico) {
        HistoricoClienteDTO dto = new HistoricoClienteDTO();
        dto.setIdHistorico(historico.getIdHistorico());
        dto.setIdCliente(historico.getIdCliente());
        dto.setCampoAlterado(historico.getCampoAlterado());
        dto.setValorAntigo(historico.getValorAntigo());
        dto.setValorNovo(historico.getValorNovo());
        dto.setDataAlteracao(historico.getDataAlteracao());
        return dto;
    }

    private HistoricoCliente transformeEmEntidade(HistoricoClienteDTO dto) {
        HistoricoCliente historico = new HistoricoCliente();
        historico.setIdCliente(dto.getIdCliente());
        historico.setCampoAlterado(dto.getCampoAlterado());
        historico.setValorAntigo(dto.getValorAntigo());
        historico.setValorNovo(dto.getValorNovo());
        historico.setDataAlteracao(dto.getDataAlteracao());
        return historico;
    }
}