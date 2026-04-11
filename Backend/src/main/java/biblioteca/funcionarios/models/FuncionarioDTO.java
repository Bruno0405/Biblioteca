package biblioteca.funcionarios.models;

public class FuncionarioDTO {

    private Integer idFuncionario;
    private String nome;
    private String email;
    private Character perfil;

    public Integer getIdFuncionario() {
        return idFuncionario;
    }
    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Character getPerfil() {
        return perfil;
    }
    public void setPerfil(Character perfil) {
        this.perfil = perfil;
    }
}