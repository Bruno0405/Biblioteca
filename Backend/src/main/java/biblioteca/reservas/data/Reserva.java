package biblioteca.reservas.data;

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

    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "id_funcionario_retirada")
    private Integer idFuncionarioRetirada;

    @Column(name = "id_funcionario_devolucao")
    private Integer idFuncionarioDevolucao;

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

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Integer getIdFuncionarioRetirada() {
        return idFuncionarioRetirada;
    }
    public void setIdFuncionarioRetirada(Integer idFuncionarioRetirada) {
        this.idFuncionarioRetirada = idFuncionarioRetirada;
    }

    public Integer getIdFuncionarioDevolucao() {
        return idFuncionarioDevolucao;
    }
    public void setIdFuncionarioDevolucao(Integer idFuncionarioDevolucao) {
        this.idFuncionarioDevolucao = idFuncionarioDevolucao;
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