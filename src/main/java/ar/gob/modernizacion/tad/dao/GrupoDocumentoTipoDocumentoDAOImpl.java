package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.GrupoDocumentoTipoDocumento;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 24/07/2017.
 */

@Repository
public class GrupoDocumentoTipoDocumentoDAOImpl extends GeneralDAO implements GrupoDocumentoTipoDocumentoDAO {

    private static final String ID = "ID";
    private static final String ID_GRUPO_DOCUMENTO="ID_GRUPO_DOCUMENTO";
    private static final String ID_TIPO_DOCUMENTO="ID_TIPO_DOCUMENTO";
    private static final String OBLIGATORIO="OBLIGATORIO";
    private static final String ORDEN="ORDEN";

    private static List<GrupoDocumentoTipoDocumento> grupoDocumentoTipoDocumentosCache;


    public GrupoDocumentoTipoDocumentoDAOImpl() {
        table = DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(ID_GRUPO_DOCUMENTO);
        columns.add(ID_TIPO_DOCUMENTO);
        columns.add(OBLIGATORIO);
        columns.add(ORDEN);
    }

    @Override
    public synchronized GrupoDocumentoTipoDocumento insert(GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        grupoDocumentoTipoDocumento.setId(id);
        String sql = getInsertStatement();

        jdbcTemplate.update(sql, grupoDocumentoTipoDocumento.getId(), grupoDocumentoTipoDocumento.getIdGrupoDocumento(),
                grupoDocumentoTipoDocumento.getIdTipoDocumento(), grupoDocumentoTipoDocumento.getObligatorio(),
                grupoDocumentoTipoDocumento.getOrden());

        if (grupoDocumentoTipoDocumentosCache != null)
            grupoDocumentoTipoDocumentosCache.add(grupoDocumentoTipoDocumento);

        return grupoDocumentoTipoDocumento;
    }

    @Override
    public GrupoDocumentoTipoDocumento update(GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = getUpdateStatement();

        jdbcTemplate.update(sql, grupoDocumentoTipoDocumento.getIdGrupoDocumento(),
                grupoDocumentoTipoDocumento.getIdTipoDocumento(), grupoDocumentoTipoDocumento.getObligatorio(),
                grupoDocumentoTipoDocumento.getOrden(), grupoDocumentoTipoDocumento.getId());

        for(int index = 0; index < grupoDocumentoTipoDocumentosCache.size(); index++) {
            if (grupoDocumentoTipoDocumentosCache.get(index).getIdGrupoDocumento() == grupoDocumentoTipoDocumento.getIdGrupoDocumento()
                    &&
                    grupoDocumentoTipoDocumentosCache.get(index).getIdTipoDocumento() == grupoDocumentoTipoDocumento.getIdTipoDocumento()) {
                grupoDocumentoTipoDocumentosCache.set(index, grupoDocumentoTipoDocumento);
                break;
            }
        }

        return grupoDocumentoTipoDocumento;
    }

    @Override
    public void delete(int idGrupoDocumento, int idTipoDocumento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "DELETE FROM " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO +
                " WHERE " + ID_GRUPO_DOCUMENTO + " = " + idGrupoDocumento + " AND " +
                ID_TIPO_DOCUMENTO + " = " + idTipoDocumento;

        jdbcTemplate.update(sql);

        for(int index = 0; index < grupoDocumentoTipoDocumentosCache.size(); index++) {
            if (grupoDocumentoTipoDocumentosCache.get(index).getIdGrupoDocumento() == idGrupoDocumento
                    &&
                    grupoDocumentoTipoDocumentosCache.get(index).getIdTipoDocumento() == idTipoDocumento) {
                grupoDocumentoTipoDocumentosCache.remove(index);
                break;
            }
        }
    }

    @Override
    public List<GrupoDocumentoTipoDocumento> list(int idGrupoDocumento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        if (grupoDocumentoTipoDocumentosCache != null)
            return grupoDocumentoTipoDocumentosCache;

        String sql = getSelectStatement(ID_GRUPO_DOCUMENTO + " = " + idGrupoDocumento);
        List<GrupoDocumentoTipoDocumento> listGrupoDocumentoTipoDocumento = jdbcTemplate.query(sql, (rs, rowNum) -> {
            GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento = new GrupoDocumentoTipoDocumento();

            executeSelectQuery(grupoDocumentoTipoDocumento, rs);
            return grupoDocumentoTipoDocumento;
        });

        grupoDocumentoTipoDocumentosCache = listGrupoDocumentoTipoDocumento;

        return grupoDocumentoTipoDocumentosCache;
    }

    private void executeSelectQuery(GrupoDocumentoTipoDocumento grupoDocumentoTipoDocumento, ResultSet rs) throws SQLException {
        grupoDocumentoTipoDocumento.setId(rs.getInt(ID));
        grupoDocumentoTipoDocumento.setIdGrupoDocumento(rs.getInt(ID_GRUPO_DOCUMENTO));
        grupoDocumentoTipoDocumento.setIdTipoDocumento(rs.getInt(ID_TIPO_DOCUMENTO));
        grupoDocumentoTipoDocumento.setObligatorio(rs.getByte(OBLIGATORIO));
        grupoDocumentoTipoDocumento.setOrden(rs.getByte(ORDEN));
    }
}
