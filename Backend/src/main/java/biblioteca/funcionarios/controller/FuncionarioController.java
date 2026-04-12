package biblioteca.funcionarios.controller;

import biblioteca.funcionarios.data.Funcionario;
import biblioteca.funcionarios.models.FuncionarioDTO;
import biblioteca.funcionarios.repository.RepositorioFuncionarios;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/funcionarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuncionarioController {

    @Inject
    RepositorioFuncionarios repositorioFuncionarios;

    @GET
    public Response listarTodos() {
        List<Funcionario> funcionarios = repositorioFuncionarios.listAll();
        List<FuncionarioDTO> funcionariosDTO = funcionarios.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(funcionariosDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Funcionario funcionario = repositorioFuncionarios.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(funcionario)).build();
    }

    @POST
    @Transactional
    public Response criar(FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = transformeEmEntidade(funcionarioDTO);
        repositorioFuncionarios.persist(funcionario);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(funcionario))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = repositorioFuncionarios.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        funcionario.setNome(funcionarioDTO.getNome());
        funcionario.setEmail(funcionarioDTO.getEmail());
        funcionario.setPerfil(funcionarioDTO.getPerfil());
        repositorioFuncionarios.persist(funcionario);
        return Response.ok(transformeEmDto(funcionario)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioFuncionarios.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    private FuncionarioDTO transformeEmDto(Funcionario funcionario) {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setIdFuncionario(funcionario.getIdFuncionario());
        dto.setNome(funcionario.getNome());
        dto.setEmail(funcionario.getEmail());
        dto.setPerfil(funcionario.getPerfil());
        return dto;
    }

    private Funcionario transformeEmEntidade(FuncionarioDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setPerfil(dto.getPerfil());
        funcionario.setSenha(dto.getSenha());
        return funcionario;
    }
}