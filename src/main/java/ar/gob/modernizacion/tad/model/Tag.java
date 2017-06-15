package ar.gob.modernizacion.tad.model;

/**
 * Created by martinm on 22/05/17.
 */
public class Tag {

    private String tag;
    private String categoria;
    private boolean selected;

    public Tag(String tag, String categoria) {
        this.tag = tag;
        this.categoria = categoria;
        this.selected = false;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
