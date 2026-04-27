package biblioteca.clientes.controller;

import biblioteca.clientes.data.Cliente;
import biblioteca.clientes.models.ClienteDTO;
import biblioteca.clientes.repository.RepositorioClientes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteController {

    @Inject
    RepositorioClientes repositorioClientes;

    @GET
    public Response listarTodos() {
        List<Cliente> clientes = repositorioClientes.listAll();
        List<ClienteDTO> clientesDTO = clientes.stream()
                .map(this::transformeEmDto)
                .toList();
        return Response.ok(clientesDTO).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id) {
        Cliente cliente = repositorioClientes.findById(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(transformeEmDto(cliente)).build();
    }

    @POST
    @Transactional
    public Response criar(ClienteDTO clienteDTO) {
        Cliente cliente = transformeEmEntidade(clienteDTO);
        repositorioClientes.persist(cliente);
        return Response
                .status(Response.Status.CREATED)
                .entity(transformeEmDto(cliente))
                .build();
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        Cliente cliente = repositorioClientes.find("email", email).firstResult();

        if (cliente == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Cliente não encontrado.")
                    .build();
        }

        // Verifica se está bloqueado
        if (Boolean.TRUE.equals(cliente.getBloqueado())) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Cliente bloqueado por excesso de tentativas de login.")
                    .build();
        }

        // Verifica a senha
        if (!senha.equals(cliente.getSenhaCliente())) {
            int tentativas = cliente.getTentativasLogin() + 1;
            cliente.setTentativasLogin(tentativas);

            // Bloqueia após 5 tentativas
            if (tentativas >= 5) {
                cliente.setBloqueado(true);
                repositorioClientes.persist(cliente);
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("Cliente bloqueado após 5 tentativas de login.")
                        .build();
            }

            repositorioClientes.persist(cliente);
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Senha incorreta. Tentativas: " + tentativas + "/5")
                    .build();
        }

        // Login com sucessi zera as tentativas
        cliente.setTentativasLogin(0);
        repositorioClientes.persist(cliente);

        return Response.ok(transformeEmDto(cliente)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Integer id, ClienteDTO clienteDTO) {
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
        repositorioClientes.persist(cliente);
        return Response.ok(transformeEmDto(cliente)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Integer id) {
        boolean deletado = repositorioClientes.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
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