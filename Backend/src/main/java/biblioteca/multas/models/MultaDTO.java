package biblioteca.multas.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MultaDTO {

    private Integer idMulta;
    private Integer idReserva;
    private BigDecimal valorMulta;
    private LocalDate dataMulta;
    private String statusMulta;
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