package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Prevalidacion;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 03/07/2017.
 */
public interface PrevalidacionDAO {


    public Prevalidacion insert(Prevalidacion prevalidacion, User user);

    public Prevalidacion get(int prevalidacionId);

    public List<Prevalidacion> listPorTramite(int tramiteId, User user);

}
