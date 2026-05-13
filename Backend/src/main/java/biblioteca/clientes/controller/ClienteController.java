package biblioteca.clientes.controller;

import biblioteca.auth.AuthException;
import biblioteca.auth.TokenService;
import biblioteca.auth.models.LoginResponse;
import biblioteca.clientes.data.Cliente;
import biblioteca.clientes.models.ClienteDTO;
import biblioteca.clientes.repository.RepositorioClientes;
import biblioteca.logs.data.Log;
import biblioteca.logs.services.LogService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ClienteController {

    @Inject
    RepositorioClientes repositorioClientes;

    @Inject
    LogService logService;

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed("admin")
    public Response listarTodos() {
        List<Cliente> clientes = repositorioClientes.listAll();
        List<ClienteDTO> clientesDTO = clientes.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(clientesDTO).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"cliente", "admin"})
    public Response buscarPorId(@PathParam("id") Integer id) {
        if (isClienteOwnershipDenied(id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Cliente cliente = repositorioClientes.findById(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(cliente)).build();
    }

    @POST
    @RolesAllowed({"gerente", "admin"})
    @Transactional
    public Response criar(ClienteDTO clienteDTO) {
        Cliente cliente = transformeEmEntidade(clienteDTO);
        if (cliente.getSenhaCliente() != null && !cliente.getSenhaCliente().isEmpty()) {
            cliente.setSenhaCliente(TokenService.hashPassword(cliente.getSenhaCliente()));
        }
        repositorioClientes.persist(cliente);

        Log logEntry = new Log();
        logEntry.setAcao("Criou cliente");
        logService.log(logEntry);

        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(cliente))
                .build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Transactional
    public Response login(Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        try {
            LoginResponse tokenResponse = tokenService.authenticate(email, senha, "cliente");
            if (tokenResponse == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Credenciais inválidas.")
                        .build();
            }

            Log logEntry = new Log();
            logEntry.setAcao("Login de cliente (id: " + tokenResponse.getUserId() + ")");
            logService.log(logEntry);

            return Response.ok(tokenResponse).build();
        } catch (AuthException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"cliente", "admin"})
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, ClienteDTO clienteDTO) {
        if (isClienteOwnershipDenied(id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Cliente cliente = repositorioClientes.findById(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cliente.setNomeCliente(clienteDTO.getNomeCliente());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setDataNascimento(clienteDTO.getDataNascimento());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setBloqueado(clienteDTO.getBloqueado());
        cliente.setTentativasLogin(clienteDTO.getTentativasLogin());
        cliente.setEmailConfirmado(clienteDTO.getEmailConfirmado());

        if (clienteDTO.getSenhaCliente() != null && !clienteDTO.getSenhaCliente().isEmpty()) {
            cliente.setSenhaCliente(TokenService.hashPassword(clienteDTO.getSenhaCliente()));
        }

        repositorioClientes.persist(cliente);

        Log logEntry = new Log();
        logEntry.setAcao("Atualizou cliente (id: " + id + ")");
        logService.log(logEntry);

        return Response.ok(transformeEmDto(cliente)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioClientes.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log logEntry = new Log();
        logEntry.setAcao("Removeu cliente (id: " + id + ")");
        logService.log(logEntry);

        return Response.noContent().build();
    }

    private boolean isClienteOwnershipDenied(Integer targetId) {
        String userType = jwt.getClaim("userType");
        if ("cliente".equals(userType)) {
            Integer userId = Integer.valueOf(jwt.getClaim("userId").toString());
            return !userId.equals(targetId);
        }
        return false;
    }

    private ClienteDTO transformeEmDto(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNomeCliente(cliente.getNomeCliente());
        dto.setCpf(cliente.getCpf());
        dto.setDataNascimento(cliente.getDataNascimento());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setEndereco(cliente.getEndereco());
        dto.setBloqueado(cliente.getBloqueado());
        dto.setTentativasLogin(cliente.getTentativasLogin());
        dto.setEmailConfirmado(cliente.getEmailConfirmado());
        return dto;
    }

    private Cliente transformeEmEntidade(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNomeCliente(dto.getNomeCliente());
        cliente.setCpf(dto.getCpf());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setEndereco(dto.getEndereco());
        cliente.setBloqueado(dto.getBloqueado() != null ? dto.getBloqueado() : false);
        cliente.setTentativasLogin(dto.getTentativasLogin() != null ? dto.getTentativasLogin() : 0);
        cliente.setEmailConfirmado(dto.getEmailConfirmado() != null ? dto.getEmailConfirmado() : false);
        cliente.setSenhaCliente(dto.getSenhaCliente());
        return cliente;
    }
}