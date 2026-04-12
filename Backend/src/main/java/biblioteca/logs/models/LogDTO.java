package biblioteca.logs.models;

import java.time.LocalDateTime;

public class LogDTO {

    private Integer idLog;
    private Integer idCliente;
    private Integer idFuncionario;
    private String acao;
    private LocalDateTime dataAcao;
    private String ip;

    public Integer getIdLog() {
        return idLog;
    }
    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }
    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getAcao() {
        return acao;
    }
    public void setAcao(String acao) {
        this.acao = acao;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }
    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
}