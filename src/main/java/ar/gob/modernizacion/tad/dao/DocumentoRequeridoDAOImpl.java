package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.DocumentoRequerido;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.dom.DOMCryptoContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 21/07/2017.
 */

@Repository
public class DocumentoRequeridoDAOImpl extends GeneralDAO implements DocumentoRequeridoDAO {

    private static String ID="ID";
    private static String ID_TIPO_TRAMITE="ID_TIPO_TRAMITE";
    private static String ID_TIPO_DOCUMENTO="ID_TIPO_DOCUMENTO";
    private static String OBLIGATORIO="OBLIGATORIO";
    private static String CANTIDAD="CANTIDAD";
    private static String ORDEN="ORDEN";
    private static String USUARIO_CREACION="USUARIO_CREACION";

    public DocumentoRequeridoDAOImpl() {
        table = DBTables.TAD_DOCUMENTO_REQUERIDO;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(ID_TIPO_TRAMITE);
        columns.add(ID_TIPO_DOCUMENTO);
        columns.add(OBLIGATORIO);
        columns.add(CANTIDAD);
        columns.add(ORDEN);
        columns.add(USUARIO_CREACION);
    }
    @Override
    public synchronized DocumentoRequerido insert(DocumentoRequerido documentoRequerido, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        documentoRequerido.setId(id);
        documentoRequerido.setUsuarioCreacion(user.getUsername());
        String sql = getInsertStatement();

        jdbcTemplate.update(sql, documentoRequerido.getId(), documentoRequerido.getIdTipoTramite(), documentoRequerido.getIdTipoDocumento(),
                documentoRequerido.getObligatorio(), documentoRequerido.getCantidad(), documentoRequerido.getOrden(),
                documentoRequerido.getUsuarioCreacion());

        return documentoRequerido;
    }

    @Override
    public synchronized DocumentoRequerido update(DocumentoRequerido documentoRequerido, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        documentoRequerido.setUsuarioCreacion(user.getUsername());

        String sql = getUpdateStatement();

        jdbcTemplate.update(sql, documentoRequerido.getIdTipoTramite(), documentoRequerido.getIdTipoDocumento(),
                documentoRequerido.getObligatorio(), documentoRequerido.getCantidad(), documentoRequerido.getOrden(),
                documentoRequerido.getUsuarioCreacion(), documentoRequerido.getId());

        return documentoRequerido;
    }

    @Override
    public void delete(int documentoId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "DELETE FROM " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                " WHERE " + ID_TIPO_DOCUMENTO + " = " + documentoId;

        jdbcTemplate.update(sql);
    }

    @Override
    public DocumentoRequerido get(int documentoRequeridoId) {
        return null;
    }

    @Override
    public List<DocumentoRequerido> listPorTramite(int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = getSelectStatement("ID_TIPO_TRAMITE = " + tramiteId);

        List<DocumentoRequerido> listDocumento = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DocumentoRequerido documentoRequerido = new DocumentoRequerido();

            executeSelectQuery(documentoRequerido, rs);
            return documentoRequerido;
        });

        return listDocumento;
    }

    private void executeSelectQuery(DocumentoRequerido documentoRequerido, ResultSet rs) throws SQLException {
        documentoRequerido.setId(rs.getInt(ID));
        documentoRequerido.setIdTipoTramite(rs.getInt(ID_TIPO_TRAMITE));
        documentoRequerido.setIdTipoDocumento(rs.getInt(ID_TIPO_DOCUMENTO));
        documentoRequerido.setObligatorio(rs.getByte(OBLIGATORIO));
        documentoRequerido.setCantidad(rs.getByte(CANTIDAD));
        documentoRequerido.setOrden(rs.getByte(ORDEN));
        documentoRequerido.setUsuarioCreacion(USUARIO_CREACION);
    }
}
