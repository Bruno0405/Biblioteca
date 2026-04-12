package biblioteca.logs.controller;

import biblioteca.logs.data.Log;
import biblioteca.logs.models.LogDTO;
import biblioteca.logs.repository.RepositorioLogs;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogController {

    @Inject
    RepositorioLogs repositorioLogs;

    @GET
    public Response listarTodos() {
        List<Log> logs = repositorioLogs.listAll();
        List<LogDTO> logsDTO = logs.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(logsDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Log log = repositorioLogs.findById(id);
        if (log == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(log)).build();
    }

    @POST
    @Transactional
    public Response criar(LogDTO logDTO) {
        Log log = transformeEmEntidade(logDTO);
        repositorioLogs.persist(log);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(log))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioLogs.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private LogDTO transformeEmDto(Log log) {
        LogDTO dto = new LogDTO();
        dto.setIdLog(log.getIdLog());
        dto.setIdCliente(log.getIdCliente());
        dto.setIdFuncionario(log.getIdFuncionario());
        dto.setAcao(log.getAcao());
        dto.setDataAcao(log.getDataAcao());
        dto.setIp(log.getIp());
        return dto;
    }

    private Log transformeEmEntidade(LogDTO dto) {
        Log log = new Log();
        log.setIdCliente(dto.getIdCliente());
        log.setIdFuncionario(dto.getIdFuncionario());
        log.setAcao(dto.getAcao());
        log.setDataAcao(dto.getDataAcao() != null ? dto.getDataAcao() : LocalDateTime.now());
        log.setIp(dto.getIp());
        return log;
    }
}