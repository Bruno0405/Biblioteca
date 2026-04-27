package biblioteca.reservas.models;

import java.time.LocalDate;

public class ReservaDTO {

    private Integer idReserva;
    private Integer idCliente;
    private Integer idLivro;
    private Integer idFuncionarioRetirada;
    private Integer idFuncionarioDevolucao;
    private LocalDate dataReserva;
    private LocalDate dataLimiteRetirada;
    private LocalDate dataRetirada;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private String statusReserva;
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
        this.idCliente = idCliente; }

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