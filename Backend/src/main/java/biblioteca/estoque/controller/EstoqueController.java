package biblioteca.estoque.controller;

import biblioteca.estoque.data.Estoque;
import biblioteca.estoque.models.EstoqueDTO;
import biblioteca.estoque.repository.RepositorioEstoque;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@Path("/estoque")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstoqueController {

    @Inject
    RepositorioEstoque repositorioEstoque;

    @GET
    public Response listarTodos(
            @QueryParam("idLivro") Integer idLivro,
            @QueryParam("disponivel") Boolean disponivel) {

        List<Estoque> estoques;

        if (idLivro != null) {
            estoques = repositorioEstoque.list("idLivro", idLivro);
        } else if (disponivel != null && disponivel) {
            estoques = repositorioEstoque.list("quantidadeTotal - quantidadeReservada - quantidadeEmprestada > 0");
        } else {
            estoques = repositorioEstoque.listAll();
        }

        List<EstoqueDTO> estoquesDTO = estoques.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(estoquesDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Estoque estoque = repositorioEstoque.findById(id);
        if (estoque == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(estoque)).build();
    }

    @POST
    @Transactional
    public Response criar(EstoqueDTO estoqueDTO) {
        Estoque estoque = transformeEmEntidade(estoqueDTO);
        repositorioEstoque.persist(estoque);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(estoque))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, EstoqueDTO estoqueDTO) {
        Estoque estoque = repositorioEstoque.findById(id);
        if (estoque == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        estoque.setIdLivro(estoqueDTO.getIdLivro());
        estoque.setQuantidadeTotal(estoqueDTO.getQuantidadeTotal());
        estoque.setQuantidadeReservada(estoqueDTO.getQuantidadeReservada());
        estoque.setQuantidadeEmprestada(estoqueDTO.getQuantidadeEmprestada());
        estoque.setQuantidadeDanificada(estoqueDTO.getQuantidadeDanificada());
        estoque.setEstoqueMinimo(estoqueDTO.getEstoqueMinimo());
        repositorioEstoque.persist(estoque);
        return Response.ok(transformeEmDto(estoque)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioEstoque.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private EstoqueDTO transformeEmDto(Estoque estoque) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setIdEstoque(estoque.getIdEstoque());
        dto.setIdLivro(estoque.getIdLivro());
        dto.setQuantidadeTotal(estoque.getQuantidadeTotal());
        dto.setQuantidadeReservada(estoque.getQuantidadeReservada());
        dto.setQuantidadeEmprestada(estoque.getQuantidadeEmprestada());
        dto.setQuantidadeDanificada(estoque.getQuantidadeDanificada());
        dto.setEstoqueMinimo(estoque.getEstoqueMinimo());
        return dto;
    }

    private Estoque transformeEmEntidade(EstoqueDTO dto) {
        Estoque estoque = new Estoque();
        estoque.setIdLivro(dto.getIdLivro());
        estoque.setQuantidadeTotal(dto.getQuantidadeTotal());
        estoque.setQuantidadeReservada(dto.getQuantidadeReservada());
        estoque.setQuantidadeEmprestada(dto.getQuantidadeEmprestada());
        estoque.setQuantidadeDanificada(dto.getQuantidadeDanificada());
        estoque.setEstoqueMinimo(dto.getEstoqueMinimo());
        return estoque;
    }
}