package biblioteca.multas.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Multas")
public class Multa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_multa")
    private Integer idMulta;

    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "valor_multa")
    private BigDecimal valorMulta;

    @Column(name = "data_multa")
    private LocalDate dataMulta;

    @Column(name = "status_multa")
    private String statusMulta;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    public Integer getIdMulta() {
        return idMulta;
    }
    public void setIdMulta(Integer idMulta) {
        this.idMulta = idMulta;
    }

    public Integer getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }
    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public LocalDate getDataMulta() {
        return dataMulta;
    }
    public void setDataMulta(LocalDate dataMulta) {
        this.dataMulta = dataMulta;
    }

    public String getStatusMulta() {
        return statusMulta;
    }
    public void setStatusMulta(String statusMulta) {
        this.statusMulta = statusMulta;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }
    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}