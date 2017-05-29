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
    private char es_embebido;
    private String usuarioCreacion;

    public Documento(int id, String acronimoGedo, String acronimoTad, String nombre, String descripcion, char es_embebido, String usuarioCreacion) {
        this.id = id;
        this.acronimoGedo = acronimoGedo;
        this.acronimoTad = acronimoTad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.es_embebido = es_embebido;
        this.usuarioCreacion = usuarioCreacion;
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

    public char getEs_embebido() {
        return es_embebido;
    }

    public void setEs_embebido(char es_embebido) {
        this.es_embebido = es_embebido;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }
}
