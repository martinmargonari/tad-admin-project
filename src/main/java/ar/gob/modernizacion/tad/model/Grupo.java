package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 01/06/2017.
 */
public class Grupo {

    private int id;
    private String descripcion;

    public Grupo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
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
}
