package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 24/07/2017.
 */
public class GrupoDocumentoTipoDocumento {

    private int id;
    private int idGrupoDocumento;
    private int idTipoDocumento;
    private byte obligatorio;
    private byte orden;
    private String nombreDocumento;

    public GrupoDocumentoTipoDocumento() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGrupoDocumento() {
        return idGrupoDocumento;
    }

    public void setIdGrupoDocumento(int idGrupoDocumento) {
        this.idGrupoDocumento = idGrupoDocumento;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public byte getObligatorio() {
        return obligatorio;
    }

    public void setObligatorio(byte obligatorio) {
        this.obligatorio = obligatorio;
    }

    public byte getOrden() {
        return orden;
    }

    public void setOrden(byte orden) {
        this.orden = orden;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }
}
