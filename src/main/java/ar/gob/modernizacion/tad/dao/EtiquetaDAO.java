package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MMargonari on 20/07/2017.
 */
public interface EtiquetaDAO {

    public Tag insert(Tag etiqueta, User user);

    public HashMap<String,List<Tag>> list(User user);

    public HashMap<String,List<Tag>> list(User user, String[] etiquetasSeleccionadas);
}
