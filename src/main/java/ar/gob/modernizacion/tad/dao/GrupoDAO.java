package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 24/07/2017.
 */
public interface GrupoDAO {

    Grupo get(int grupoId, User user);

    Grupo insert(Grupo grupo, User user);

    Grupo update(Grupo grupo, User user);

    List<Grupo> list(User user);

}
