package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.GrupoDocumentoTipoDocumento;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 24/07/2017.
 */
public interface GrupoDocumentoTipoDocumentoDAO {

    GrupoDocumentoTipoDocumento insert(GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento, User user);

    GrupoDocumentoTipoDocumento update(GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento, User user);

    void delete(int idGrupoDocuento, int idTipoDocumento, User user);

    List<GrupoDocumentoTipoDocumento> list(int idGrupoDocumento, User user);
}
