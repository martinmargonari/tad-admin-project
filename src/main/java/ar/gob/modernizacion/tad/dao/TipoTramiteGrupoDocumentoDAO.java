package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 24/07/2017.
 */
public interface TipoTramiteGrupoDocumentoDAO {

    void insert(int grupoId, int tramiteId, User user);

    void delete(int grupoId, int tramiteId, User user);

    List<Integer> list(int tramiteId, User user);
}
