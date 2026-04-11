package biblioteca.funcionarios.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Funcionarios")
public class Funcionario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    private Integer idFuncionario;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "perfil")
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
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Character getPerfil() {
        return perfil;
    }
    public void setPerfil(Character perfil) {
        this.perfil = perfil;
    }
}