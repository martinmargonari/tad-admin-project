package ar.gob.modernizacion.tad.model;

/**
 * Created by martinm on 29/05/17.
 */

public class Documento {

    private int id;
    private String acronimoGedo;
    private String acronimoTad;
    private String nombre;
    private String descripcion;
    private byte esEmbebido;
    private String usuarioCreacion;
    private byte relacionado;
    public Relacion relacion;

    private class Relacion {
        public byte obligatorio;
        public byte cantidad;
        public byte orden;
        public String usuario;

        public Relacion(byte obligatorio, byte cantidad, byte orden, String usuario) {
            this.obligatorio = obligatorio;
            this.cantidad = cantidad;
            this.orden = orden;
            this.usuario = usuario;
        }
    }

    public Documento(int id, String acronimoGedo, String acronimoTad, String nombre, String descripcion, byte es_embebido, String usuarioCreacion) {
        this.id = id;
        this.acronimoGedo = acronimoGedo;
        this.acronimoTad = acronimoTad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esEmbebido = es_embebido;
        this.usuarioCreacion = usuarioCreacion;
        this.relacionado = 0;
        this.relacion = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getEsEmbebido() {
        return esEmbebido;
    }

    public void setEsEmbebido(byte esEmbebido) {
        this.esEmbebido = esEmbebido;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public byte getRelacionado() {
        return relacionado;
    }

    public void setRelacionado(byte relacionado) {
        this.relacionado = relacionado;
    }

    public Relacion getRelacion() {
        return relacion;
    }

    public void setRelacion(byte obligatorio, byte cantidad, byte orden, String usuario) {
        this.relacion = new Relacion(obligatorio,cantidad,orden,usuario);
    }
}
