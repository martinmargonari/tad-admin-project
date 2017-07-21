package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.User;

import java.util.List;

/**
 * Created by MMargonari on 21/07/2017.
 */
public interface DocumentoDAO {

    public Documento get(int documentoId, User user);

    public Documento insert(Documento documento, User user);

    public Documento update(Documento documento, User user);

    public String getAcronimosTad(User user);

    public List<Documento> list(User user);
}
