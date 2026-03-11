package biblioteca.livros.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Livros")
public class Livro extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "nome_livro")
    private String nomeLivro;

    @Column(name = "isnb")
    private String isnb;

    @Column(name = "editora")
    private String editora;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "sinopse")
    private String sinopse;

    @Column(name = "localizacao_fisica")
    private String localizacaoFisica;

    public Integer getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getIsnb() {
        return isnb;
    }

    public void setIsnb(String isnb) {
        this.isnb = isnb;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getLocalizacaoFisica() {
        return localizacaoFisica;
    }

    public void setLocalizacaoFisica(String localizacaoFisica) {
        this.localizacaoFisica = localizacaoFisica;
    }
}
