package ar.gob.modernizacion.tad.model;

import java.util.ArrayList;

/**
 * Created by MMargonari on 26/05/2017.
 */
public class Tramite {

    private int id;
    private String descripcion;
    private char idTramiteConfiguracion;
    private String usuarioCreacion;
    private String trata;
    private String usuarioIniciador;
    private String reparticion;
    private String sector;
    private String nombre;
    private String etiquetas;
    private char pago;
    private String idTipoTramiteSir;
    private String descripcionHtml;
    private char obligatorioInterviniente;
    private char prevalidacion;
    private char visible;


    public Tramite(int id, String descripcion, char idTramiteConfiguracion, String usuarioCreacion, String trata, String usuarioIniciador, String reparticion, String sector, String nombre, String etiquetas, char pago, String idTipoTramiteSir, String descripcionHtml, char obligatorioInterviniente, char prevalidacion, char visible) {
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

    public char getIdTramiteConfiguracion() {
        return idTramiteConfiguracion;
    }

    public void setIdTramiteConfiguracion(char idTramiteConfiguracion) {
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

    public char getPago() {
        return pago;
    }

    public void setPago(char pago) {
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

    public char getPrevalidacion() {
        return prevalidacion;
    }

    public void setPrevalidacion(char prevalidacion) {
        this.prevalidacion = prevalidacion;
    }

    public char getVisible() {
        return visible;
    }

    public void setVisible(char visible) {
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

    public char getObligatorioInterviniente() {
        return obligatorioInterviniente;
    }

    public void setObligatorioInterviniente(char obligatorioInterviniente) {
        this.obligatorioInterviniente = obligatorioInterviniente;
    }
}
