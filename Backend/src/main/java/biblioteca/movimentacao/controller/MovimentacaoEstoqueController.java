package biblioteca.movimentacao.controller;

import biblioteca.movimentacao.data.MovimentacaoEstoque;
import biblioteca.movimentacao.models.MovimentacaoEstoqueDTO;
import biblioteca.movimentacao.repository.RepositorioMovimentacaoEstoque;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/movimentacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MovimentacaoEstoqueController {

    @Inject
    RepositorioMovimentacaoEstoque repositorioMovimentacaoEstoque;

    @Inject
    LogService logService;

    @GET
    @RolesAllowed({"funcionario", "gerente", "admin"})
    public Response listarTodos() {
        List<MovimentacaoEstoque> movimentacoes = repositorioMovimentacaoEstoque.listAll();
        List<MovimentacaoEstoqueDTO> movimentacoesDTO = movimentacoes.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(movimentacoesDTO).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"funcionario", "gerente", "admin"})
    public Response buscarPorId(@PathParam("id") Integer id) {
        MovimentacaoEstoque movimentacao = repositorioMovimentacaoEstoque.findById(id);
        if (movimentacao == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(movimentacao)).build();
    }

    @POST
    @RolesAllowed({"funcionario", "gerente", "admin"})
    @Transactional
    public Response criar(MovimentacaoEstoqueDTO movimentacaoDTO) {
        MovimentacaoEstoque movimentacao = transformeEmEntidade(movimentacaoDTO);
        repositorioMovimentacaoEstoque.persist(movimentacao);

        Log logEntry = new Log();
        logEntry.setAcao("Criou movimentacao");
        logService.log(logEntry);

        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(movimentacao))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioMovimentacaoEstoque.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu movimentacao (id: " + id + ")");
        logService.log(logEntry);

        return Response.noContent().build();
    }

    private MovimentacaoEstoqueDTO transformeEmDto(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO();
        dto.setIdMovimentacao(movimentacao.getIdMovimentacao());
        dto.setIdLivro(movimentacao.getIdLivro());
        dto.setTipoMovimentacao(movimentacao.getTipoMovimentacao());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setDataMovimentacao(movimentacao.getDataMovimentacao());
        dto.setMotivo(movimentacao.getMotivo());
        return dto;
    }

    private MovimentacaoEstoque transformeEmEntidade(MovimentacaoEstoqueDTO dto) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setIdLivro(dto.getIdLivro());
        movimentacao.setTipoMovimentacao(dto.getTipoMovimentacao());
        movimentacao.setQuantidade(dto.getQuantidade());
        movimentacao.setDataMovimentacao(dto.getDataMovimentacao());
        movimentacao.setMotivo(dto.getMotivo());
        return movimentacao;
    }
}