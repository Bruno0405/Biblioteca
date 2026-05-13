package biblioteca.auth.controller;

import biblioteca.auth.AuthException;
import biblioteca.auth.TokenService;
import biblioteca.auth.models.LoginRequest;
import biblioteca.auth.models.LoginResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthController {

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequest request) {
        try {
            LoginResponse response = tokenService.authenticate(request.getEmail(), request.getSenha(), request.getTipo());
            if (response == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Credenciais inválidas ou conta bloqueada.")
                        .build();
            }
            return Response.ok(response).build();
        } catch (AuthException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/me")
    @Authenticated
    public Response me() {
        Map<String, Object> info = new HashMap<>();
        info.put("userId", jwt.getClaim("userId"));
        info.put("email", jwt.getClaim("upn"));
        info.put("userType", jwt.getClaim("userType"));
        info.put("perfil", jwt.getClaim("perfil"));
        info.put("groups", jwt.getGroups());
        return Response.ok(info).build();
    }
}
