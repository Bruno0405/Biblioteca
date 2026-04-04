package biblioteca.clientes.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "Clientes")
public class Cliente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    @Column(name = "senha_cliente")
    private String senhaCliente;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "data_nascimento")
    private java.time.LocalDate dataNascimento;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "email")
    private String email;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "bloqueado")
    private Boolean bloqueado;

    @Column(name = "tentativas_login")
    private Integer tentativasLogin;

    @Column(name = "email_confirmado")
    private Boolean emailConfirmado;

    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {

        this.idCliente = idCliente;
    }

    public String getNomeCliente() {

        return nomeCliente;
    }
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getSenhaCliente() {
        return senhaCliente;
    }
    public void setSenhaCliente(String senhaCliente) {
        this.senhaCliente = senhaCliente;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public java.time.LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(java.time.LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }
    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Integer getTentativasLogin() {
        return tentativasLogin;
    }
    public void setTentativasLogin(Integer tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public Boolean getEmailConfirmado() {
        return emailConfirmado;
    }
    public void setEmailConfirmado(Boolean emailConfirmado) {
        this.emailConfirmado = emailConfirmado;
    }
}