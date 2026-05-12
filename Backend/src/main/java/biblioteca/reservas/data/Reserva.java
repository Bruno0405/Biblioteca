package biblioteca.reservas.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import biblioteca.clientes.data.Cliente;
import biblioteca.funcionarios.data.Funcionario;
import biblioteca.livros.data.Livro;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Reservas")
public class Reserva extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @Column(name = "id_livro")
    private Integer idLivro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livro", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Livro livro;

    @Column(name = "id_funcionario_retirada")
    private Integer idFuncionarioRetirada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario_retirada", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Funcionario funcionarioRetirada;

    @Column(name = "id_funcionario_devolucao")
    private Integer idFuncionarioDevolucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario_devolucao", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Funcionario funcionarioDevolucao;

    @Column(name = "data_reserva")
    private LocalDate dataReserva;

    @Column(name = "data_limite_retirada")
    private LocalDate dataLimiteRetirada;

    @Column(name = "data_retirada")
    private LocalDate dataRetirada;

    @Column(name = "data_prevista_devolucao")
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Column(name = "status_reserva")
    private String statusReserva;

    @Column(name = "codigo_reserva")
    private String codigoReserva;

    public Integer getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Livro getLivro() {
        return livro;
    }
    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Integer getIdFuncionarioRetirada() {
        return idFuncionarioRetirada;
    }
    public void setIdFuncionarioRetirada(Integer idFuncionarioRetirada) {
        this.idFuncionarioRetirada = idFuncionarioRetirada;
    }

    public Funcionario getFuncionarioRetirada() {
        return funcionarioRetirada;
    }
    public void setFuncionarioRetirada(Funcionario funcionarioRetirada) {
        this.funcionarioRetirada = funcionarioRetirada;
    }

    public Integer getIdFuncionarioDevolucao() {
        return idFuncionarioDevolucao;
    }
    public void setIdFuncionarioDevolucao(Integer idFuncionarioDevolucao) {
        this.idFuncionarioDevolucao = idFuncionarioDevolucao;
    }

    public Funcionario getFuncionarioDevolucao() {
        return funcionarioDevolucao;
    }
    public void setFuncionarioDevolucao(Funcionario funcionarioDevolucao) {
        this.funcionarioDevolucao = funcionarioDevolucao;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }
    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDate getDataLimiteRetirada() {
        return dataLimiteRetirada;
    }
    public void setDataLimiteRetirada(LocalDate dataLimiteRetirada) {
        this.dataLimiteRetirada = dataLimiteRetirada;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }
    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }
    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatusReserva() {
        return statusReserva;
    }
    public void setStatusReserva(String statusReserva) {
        this.statusReserva = statusReserva;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }
    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }
}