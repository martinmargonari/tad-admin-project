package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 30/06/2017.
 */
public interface TramiteDAO {

    public Tramite get(int tramiteId, User user);

    public List<Tramite> list(User user);

}
