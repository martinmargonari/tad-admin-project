package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.DocumentoRequerido;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 21/07/2017.
 */
public interface DocumentoRequeridoDAO {

    public DocumentoRequerido insert(DocumentoRequerido documentoRequerido, User user);

    public DocumentoRequerido update(DocumentoRequerido documentoRequerido, User user);

    public void delete(int documentoId, User user);

    public DocumentoRequerido get(int documentoRequeridoId);

    public List<DocumentoRequerido> listPorTramite(int tramiteId, User user);
}
