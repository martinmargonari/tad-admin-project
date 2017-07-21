package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 03/07/2017.
 */
public class Prevalidacion {

    private int id;
    private int idTipoTramite;
    private String cuit;

    public Prevalidacion() {}

    public Prevalidacion(int id, int idTipoTramite, String cuit) {
        this.id = id;
        this.idTipoTramite = idTipoTramite;
        this.cuit = cuit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTipoTramite() {
        return idTipoTramite;
    }

    public void setIdTipoTramite(int idTipoTramite) {
        this.idTipoTramite = idTipoTramite;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
}
