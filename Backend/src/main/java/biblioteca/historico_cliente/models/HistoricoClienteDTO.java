package biblioteca.historico_cliente.models;

import java.time.LocalDate;

public class HistoricoClienteDTO {

    private Integer idHistorico;
    private Integer idCliente;
    private String campoAlterado;
    private String valorAntigo;
    private String valorNovo;
    private LocalDate dataAlteracao;

    public Integer getIdHistorico() {
        return idHistorico;
    }
    public void setIdHistorico(Integer idHistorico) {
        this.idHistorico = idHistorico;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCampoAlterado() {
        return campoAlterado;
    }
    public void setCampoAlterado(String campoAlterado) {
        this.campoAlterado = campoAlterado;
    }

    public String getValorAntigo() {
        return valorAntigo;
    }
    public void setValorAntigo(String valorAntigo) {
        this.valorAntigo = valorAntigo;
    }

    public String getValorNovo() {
        return valorNovo;
    }
    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public LocalDate getDataAlteracao() {
        return dataAlteracao;
    }
    public void setDataAlteracao(LocalDate dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}