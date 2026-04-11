package biblioteca.historico_cliente.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Historico_cliente")
public class HistoricoCliente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Integer idHistorico;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "campo_alterado")
    private String campoAlterado;

    @Column(name = "valor_antigo")
    private String valorAntigo;

    @Column(name = "valor_novo")
    private String valorNovo;

    @Column(name = "data_alteracao")
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