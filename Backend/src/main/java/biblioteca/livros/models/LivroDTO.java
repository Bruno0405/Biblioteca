package biblioteca.livros.models;

import biblioteca.autores.models.AutorDTO;
import biblioteca.generos.models.GeneroDTO;
import java.util.List;
import java.util.Set;

public class LivroDTO {

    private Integer idLivro;
    private String nomeLivro;
    private String isnb;
    private String editora;
    private Integer ano;
    private String sinopse;
    private String localizacaoFisica;

    private Set<Integer> idAutores;
    private Set<Integer> idGeneros;

    private List<AutorDTO> autores;
    private List<GeneroDTO> generos;

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

    public Set<Integer> getIdAutores() {
        return idAutores;
    }

    public void setIdAutores(Set<Integer> idAutores) {
        this.idAutores = idAutores;
    }

    public Set<Integer> getIdGeneros() {
        return idGeneros;
    }

    public void setIdGeneros(Set<Integer> idGeneros) {
        this.idGeneros = idGeneros;
    }

    public List<AutorDTO> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorDTO> autores) {
        this.autores = autores;
    }

    public List<GeneroDTO> getGeneros() {
        return generos;
    }

    public void setGeneros(List<GeneroDTO> generos) {
        this.generos = generos;
    }
}
