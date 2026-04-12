package biblioteca.movimentacao_estoque.controller;

import biblioteca.movimentacao_estoque.data.MovimentacaoEstoque;
import biblioteca.movimentacao_estoque.models.MovimentacaoEstoqueDTO;
import biblioteca.movimentacao_estoque.repository.RepositorioMovimentacaoEstoque;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/movimentacao-estoque")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovimentacaoEstoqueController {

    @Inject
    RepositorioMovimentacaoEstoque repositorioMovimentacaoEstoque;

    @GET
    public Response listarTodos() {
        List<MovimentacaoEstoque> movimentacoes = repositorioMovimentacaoEstoque.listAll();
        List<MovimentacaoEstoqueDTO> movimentacoesDTO = movimentacoes.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(movimentacoesDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        MovimentacaoEstoque movimentacao = repositorioMovimentacaoEstoque.findById(id);
        if (movimentacao == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(movimentacao)).build();
    }

    @POST
    @Transactional
    public Response criar(MovimentacaoEstoqueDTO movimentacaoDTO) {
        MovimentacaoEstoque movimentacao = transformeEmEntidade(movimentacaoDTO);
        repositorioMovimentacaoEstoque.persist(movimentacao);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(movimentacao))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioMovimentacaoEstoque.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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