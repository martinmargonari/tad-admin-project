package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 26/05/2017.
 */
public class Tramite {

    private int id;
    private String descripcion;
    private byte idTramiteConfiguracion;
    private String usuarioCreacion;
    private String trata;
    private String usuarioIniciador;
    private String reparticion;
    private String sector;
    private String nombre;
    private String etiquetas;
    private byte pago;
    private String idTipoTramiteSir;
    private String descripcionHtml;
    private byte obligatorioInterviniente;
    private byte prevalidacion;
    private byte visible;
    private String usuarioModificacion;

    public Tramite(int id, String descripcion, byte idTramiteConfiguracion, String usuarioCreacion, String trata, String usuarioIniciador, String reparticion, String sector, String nombre, String etiquetas, byte pago, String idTipoTramiteSir, String descripcionHtml, byte obligatorioInterviniente, byte prevalidacion, byte visible) {
        this.id = id;
        this.descripcion = descripcion;
        this.idTramiteConfiguracion = idTramiteConfiguracion;
        this.usuarioCreacion = usuarioCreacion;
        this.trata = trata;
        this.usuarioIniciador = usuarioIniciador;
        this.reparticion = reparticion;
        this.sector = sector;
        this.nombre = nombre;
        this.etiquetas = etiquetas;
        this.pago = pago;
        this.idTipoTramiteSir = idTipoTramiteSir;
        this.descripcionHtml = descripcionHtml;
        this.obligatorioInterviniente = obligatorioInterviniente;
        this.prevalidacion = prevalidacion;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getIdTramiteConfiguracion() {
        return idTramiteConfiguracion;
    }

    public void setIdTramiteConfiguracion(byte idTramiteConfiguracion) {
        this.idTramiteConfiguracion = idTramiteConfiguracion;
    }

    public String getTrata() {
        return trata;
    }

    public void setTrata(String trata) {
        this.trata = trata;
    }

    public String getReparticion() {
        return reparticion;
    }

    public void setReparticion(String reparticion) {
        this.reparticion = reparticion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(String etiquetas) {
        this.etiquetas = etiquetas;
    }

    public byte getPago() {
        return pago;
    }

    public void setPago(byte pago) {
        this.pago = pago;
    }

    public String getIdTipoTramiteSir() {
        return idTipoTramiteSir;
    }

    public void setIdTipoTramiteSir(String idTipoTramiteSir) {
        this.idTipoTramiteSir = idTipoTramiteSir;
    }

    public String getDescripcionHtml() {
        return descripcionHtml;
    }

    public void setDescripcionHtml(String descripcionHtml) {
        this.descripcionHtml = descripcionHtml;
    }

    public byte getPrevalidacion() {
        return prevalidacion;
    }

    public void setPrevalidacion(byte prevalidacion) {
        this.prevalidacion = prevalidacion;
    }

    public byte getVisible() {
        return visible;
    }

    public void setVisible(byte visible) {
        this.visible = visible;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public String getUsuarioIniciador() {
        return usuarioIniciador;
    }

    public void setUsuarioIniciador(String usuarioIniciador) {
        this.usuarioIniciador = usuarioIniciador;
    }

    public byte getObligatorioInterviniente() {
        return obligatorioInterviniente;
    }

    public void setObligatorioInterviniente(byte obligatorioInterviniente) {
        this.obligatorioInterviniente = obligatorioInterviniente;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }
}
