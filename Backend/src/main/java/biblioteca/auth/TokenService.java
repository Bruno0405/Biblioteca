package biblioteca.auth;

import biblioteca.clientes.data.Cliente;
import biblioteca.clientes.repository.RepositorioClientes;
import biblioteca.funcionarios.data.Funcionario;
import biblioteca.funcionarios.repository.RepositorioFuncionarios;
import biblioteca.auth.models.LoginResponse;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    @Inject
    RepositorioClientes repositorioClientes;

    @Inject
    RepositorioFuncionarios repositorioFuncionarios;

    public LoginResponse authenticate(String email, String senha, String tipo) {
        if ("cliente".equals(tipo)) {
            Cliente cliente = repositorioClientes.find("email", email).firstResult();
            if (cliente == null) return null;

            if (Boolean.TRUE.equals(cliente.getBloqueado())) {
                throw new AuthException("Conta bloqueada por excesso de tentativas.");
            }

            if (!verifyPassword(senha, cliente.getSenhaCliente())) {
                int tentativas = cliente.getTentativasLogin() + 1;
                cliente.setTentativasLogin(tentativas);
                if (tentativas >= 5) {
                    cliente.setBloqueado(true);
                }
                repositorioClientes.persist(cliente);
                return null;
            }

            cliente.setTentativasLogin(0);
            repositorioClientes.persist(cliente);

            String token = generateToken(cliente.getIdCliente(), cliente.getEmail(), "cliente", null);
            return new LoginResponse(token, cliente.getIdCliente(), cliente.getNomeCliente(), cliente.getEmail(), "cliente", null);

        } else if ("funcionario".equals(tipo)) {
            Funcionario func = repositorioFuncionarios.find("email", email).firstResult();
            if (func == null) return null;

            if (!verifyPassword(senha, func.getSenha())) return null;

            String perfilStr = String.valueOf(func.getPerfil());
            String token = generateToken(func.getIdFuncionario(), func.getEmail(), "funcionario", perfilStr);
            return new LoginResponse(token, func.getIdFuncionario(), func.getNome(), func.getEmail(), "funcionario", perfilStr);
        }

        return null;
    }

    private String generateToken(Integer userId, String email, String userType, String perfil) {
        String group;
        if ("cliente".equals(userType)) {
            group = "cliente";
        } else {
            group = mapPerfilToGroup(perfil != null ? perfil.charAt(0) : 'F');
        }

        return Jwt.issuer("biblioteca")
                .upn(email)
                .groups(new HashSet<>(Set.of(group)))
                .claim("userId", userId)
                .claim("userType", userType)
                .claim("perfil", perfil != null ? perfil : "")
                .sign();
    }

    private String mapPerfilToGroup(char perfil) {
        return switch (perfil) {
            case 'A' -> "admin";
            case 'G' -> "gerente";
            case 'F' -> "funcionario";
            default -> "funcionario";
        };
    }

    private boolean verifyPassword(String plain, String storedHash) {
        return BcryptUtil.matches(plain, storedHash);
    }

    public static String hashPassword(String plain) {
        return BcryptUtil.bcryptHash(plain);
    }
}
