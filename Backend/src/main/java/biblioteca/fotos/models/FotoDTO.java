package biblioteca.fotos.models;

public class FotoDTO {

    private Integer idFoto;
    private Integer idLivro;
    private String foto;

    public Integer getIdFoto() {
        return idFoto;
    }
    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public Integer getIdLivro() {
        return idLivro;
    }
    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }
}