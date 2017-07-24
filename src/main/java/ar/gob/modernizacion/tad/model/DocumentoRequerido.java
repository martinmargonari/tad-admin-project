package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 21/07/2017.
 */
public class DocumentoRequerido {

    private int id;
    private int idTipoTramite;
    private int idTipoDocumento;
    private byte obligatorio;
    private byte cantidad;
    private byte orden;
    private String usuarioCreacion;
    private String acronimoGedo;
    private String acronimoTad;
    private String nombre;

    public DocumentoRequerido() {}

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

    public byte getCantidad() {
        return cantidad;
    }

    public void setCantidad(byte cantidad) {
        this.cantidad = cantidad;
    }

    public byte getOrden() {
        return orden;
    }

    public void setOrden(byte orden) {
        this.orden = orden;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public String getAcronimoGedo() {
        return acronimoGedo;
    }

    public void setAcronimoGedo(String acronimoGedo) {
        this.acronimoGedo = acronimoGedo;
    }

    public String getAcronimoTad() {
        return acronimoTad;
    }

    public void setAcronimoTad(String acronimoTad) {
        this.acronimoTad = acronimoTad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
