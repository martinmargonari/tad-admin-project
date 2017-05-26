package ar.gob.modernizacion.tad.model;

import java.util.ArrayList;

/**
 * Created by MMargonari on 26/05/2017.
 */
public class Tramite {

    private int id;
    private String descripcion;
    private int idTramiteConfiguracion;
    private String trata;
    private String usuario;
    private String reparticion;
    private String sector;
    private String nombre;
    private String etiquetas;
    private char pago;
    private String idTipoTramiteSir;
    private String descripcionHtml;
    private char prevalidacion;
    private char visible;


    public Tramite(int id, String descripcion, int idTramiteConfiguracion, String trata, String usuario, String reparticion, String sector, String nombre, String etiquetas, char pago, String idTipoTramiteSir, String descripcionHtml, char prevalidacion, char visible) {
        this.id = id;
        this.descripcion = descripcion;
        this.idTramiteConfiguracion = idTramiteConfiguracion;
        this.trata = trata;
        this.usuario = usuario;
        this.reparticion = reparticion;
        this.sector = sector;
        this.nombre = nombre;
        this.etiquetas = etiquetas;
        this.pago = pago;
        this.idTipoTramiteSir = idTipoTramiteSir;
        this.descripcionHtml = descripcionHtml;
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

    public int getIdTramiteConfiguracion() {
        return idTramiteConfiguracion;
    }

    public void setIdTramiteConfiguracion(int idTramiteConfiguracion) {
        this.idTramiteConfiguracion = idTramiteConfiguracion;
    }

    public String getTrata() {
        return trata;
    }

    public void setTrata(String trata) {
        this.trata = trata;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
}
