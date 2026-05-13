package biblioteca.funcionarios.controller;

import biblioteca.auth.TokenService;
import biblioteca.auth.models.LoginResponse;
import biblioteca.funcionarios.data.Funcionario;
import biblioteca.funcionarios.models.FuncionarioDTO;
import biblioteca.funcionarios.repository.RepositorioFuncionarios;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/funcionarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuncionarioController {

    @Inject
    RepositorioFuncionarios repositorioFuncionarios;

    @Inject
    LogService logService;

    @Inject
    TokenService tokenService;

    @GET
    @RolesAllowed("admin")
    public Response listarTodos() {
        List<Funcionario> funcionarios = repositorioFuncionarios.listAll();
        List<FuncionarioDTO> funcionariosDTO = funcionarios.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(funcionariosDTO).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Funcionario funcionario = repositorioFuncionarios.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(funcionario)).build();
    }

    @POST
    @RolesAllowed("admin")
    @Transactional
    public Response criar(FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = transformeEmEntidade(funcionarioDTO);
        if (funcionario.getSenha() != null && !funcionario.getSenha().isEmpty()) {
            funcionario.setSenha(TokenService.hashPassword(funcionario.getSenha()));
        }
        repositorioFuncionarios.persist(funcionario);

        Log logEntry = new Log();
        logEntry.setAcao("Criou funcionario");
        logService.log(logEntry);

        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(funcionario))
                .build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Transactional
    public Response login(Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        LoginResponse tokenResponse = tokenService.authenticate(email, senha, "funcionario");
        if (tokenResponse == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas.")
                    .build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Login de funcionario (id: " + tokenResponse.getUserId() + ")");
        logService.log(logEntry);

        return Response.ok(tokenResponse).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, FuncionarioDTO funcionarioDTO) {
        Funcionario funcionario = repositorioFuncionarios.findById(id);
        if (funcionario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        funcionario.setNome(funcionarioDTO.getNome());
        funcionario.setEmail(funcionarioDTO.getEmail());
        funcionario.setPerfil(funcionarioDTO.getPerfil());

        if (funcionarioDTO.getSenha() != null && !funcionarioDTO.getSenha().isEmpty()) {
            funcionario.setSenha(TokenService.hashPassword(funcionarioDTO.getSenha()));
        }

        repositorioFuncionarios.persist(funcionario);

        Log logEntry = new Log();
        logEntry.setAcao("Atualizou funcionario (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(transformeEmDto(funcionario)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioFuncionarios.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu funcionario (id: " + id + ")");
        logService.log(logEntry);

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