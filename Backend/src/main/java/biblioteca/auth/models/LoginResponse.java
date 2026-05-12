package biblioteca.auth.models;

public class LoginResponse {

    private String token;
    private Integer userId;
    private String name;
    private String email;
    private String tipo;
    private String perfil;

    public LoginResponse() {}

    public LoginResponse(String token, Integer userId, String name, String email, String tipo, String perfil) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.tipo = tipo;
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
