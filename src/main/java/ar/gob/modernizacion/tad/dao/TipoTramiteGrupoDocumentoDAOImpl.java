package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 24/07/2017.
 */

@Repository
public class TipoTramiteGrupoDocumentoDAOImpl extends GeneralDAO implements TipoTramiteGrupoDocumentoDAO {

    private static final String ID_GRUPO = "ID_GRUPO";
    private static final String ID_TIPO_TRAMITE = "ID_TIPO_TRAMITE";

    public TipoTramiteGrupoDocumentoDAOImpl() {
        table = DBTables.TAD_T_TRAMITE_G_DOCUMENTO;
        columns = new ArrayList<>();
        columns.add(ID_GRUPO);
        columns.add(ID_TIPO_TRAMITE);
    }

    @Override
    public void insert(int grupoId, int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = getInsertStatement();

        jdbcTemplate.update(sql, grupoId, tramiteId);
    }

    @Override
    public void delete(int grupoId, int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "DELETE FROM " + table +
                " WHERE " + ID_GRUPO + " = " + grupoId + " AND " + ID_TIPO_TRAMITE + " = " + tramiteId;

        jdbcTemplate.update(sql);

    }

    @Override
    public List<Integer> list(int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = getSelectStatement("ID_TIPO_TRAMITE = " + tramiteId);

        List<Integer> listGrupos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getInt(ID_GRUPO);
        });

        return listGrupos;
    }
}
