package ar.gob.modernizacion.tad.model;

/**
 * Created by MMargonari on 01/06/2017.
 */
public class Grupo {

    private int id;
    private String descripcion;
    private byte relacionado;

    public Grupo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.relacionado = 0;
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

    public byte getRelacionado() {
        return relacionado;
    }

    public void setRelacionado(byte relacionado) {
        this.relacionado = relacionado;
    }
}
