package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.constants.DBTables;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by MMargonari on 30/06/2017.
 */

@Repository
public class TramiteDAOImpl extends GeneralDAO implements TramiteDAO {

    private static final String ID = "ID";
    private static final String NOMBRE = "NOMBRE";
    private static final String PREVALIDACION = "PREVALIDACION";

    public TramiteDAOImpl() {}

    public TramiteDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Tramite get(int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "SELECT ID, NOMBRE, PREVALIDACION FROM " + DBTables.TAD_TIPO_TRAMITE + " WHERE ID = " + tramiteId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Tramite>() {

            @Override
            public Tramite extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    Tramite tramite = new Tramite();
                    tramite.setId(rs.getInt(ID));
                    tramite.setNombre(rs.getString(NOMBRE));
                    tramite.setPrevalidacion(rs.getByte(PREVALIDACION));
                    return tramite;
                }
                return null;
            }
        });
    }

    @Override
    @Cacheable(cacheNames = "tramites", key = "#user.username")
    public List<Tramite> list(User user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource(user));

        String sql = "SELECT ID, NOMBRE, PREVALIDACION FROM " + DBTables.TAD_TIPO_TRAMITE + " WHERE PREVALIDACION = 1";
        List<Tramite> listTramite = jdbcTemplate.query(sql, new RowMapper<Tramite>() {
            @Override
            public Tramite mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tramite tramite = new Tramite();
                tramite.setId(rs.getInt(ID));
                tramite.setNombre(rs.getString(NOMBRE));
                tramite.setPrevalidacion(rs.getByte(PREVALIDACION));

                return tramite;
            }

        });

        return listTramite;
    }

    @Override
    public boolean testConnection(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "SELECT 1 FROM " + DBTables.TAD_TIPO_TRAMITE;
        boolean result = false;
        try {
            jdbcTemplate.queryForList(sql);
            result = true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

/*
    @CachePut("tramites")
    public List<Tramite> refreshCacheTramites() {
        String sql = "SELECT ID, NOMBRE, PREVALIDACION FROM " + DBTables.TAD_TIPO_TRAMITE + " WHERE PREVALIDACION = 1";
        List<Tramite> listTramite = jdbcTemplate.query(sql, new RowMapper<Tramite>() {
            @Override
            public Tramite mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tramite tramite = new Tramite();
                tramite.setId(rs.getInt(ID));
                tramite.setNombre(rs.getString(NOMBRE));
                tramite.setPrevalidacion(rs.getByte(PREVALIDACION));

                return tramite;
            }

        });

        return listTramite;
    }*/
}
