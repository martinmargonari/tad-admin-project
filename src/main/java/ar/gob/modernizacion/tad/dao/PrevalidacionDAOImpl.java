package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.constants.DBTables;
import ar.gob.modernizacion.tad.model.Prevalidacion;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.cache.annotation.CacheEvict;
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
 * Created by MMargonari on 03/07/2017.
 */

@Repository
public class PrevalidacionDAOImpl extends GeneralDAO implements PrevalidacionDAO {

    private static final String ID = "ID";
    private static final String ID_TIPO_TRAMITE = "ID_TIPO_TRAMITE";
    private static final String CUIT = "CUIT";

    public PrevalidacionDAOImpl() {}

    public PrevalidacionDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @CacheEvict(cacheNames = "prevalidaciones", key = "#prevalidacion.idTipoTramite")
    public synchronized Prevalidacion insert(Prevalidacion prevalidacion, User user) {
        int id = getMaxId(user) + 1;
        prevalidacion.setId(id);
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "INSERT INTO " + DBTables.TAD_PREVALIDACION + "(ID, ID_TIPO_TRAMITE, CUIT)"
                + " VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, prevalidacion.getId(), prevalidacion.getIdTipoTramite(), prevalidacion.getCuit());
        
        return prevalidacion;
    }

    @Override
    public Prevalidacion get(int id) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = "prevalidaciones", key = "#idTipoTramite")
    public List<Prevalidacion> listPorTramite(int idTipoTramite, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = "SELECT ID, CUIT FROM " + DBTables.TAD_PREVALIDACION + " WHERE ID_TIPO_TRAMITE = " + idTipoTramite;
        List<Prevalidacion> listPrevalidacion = jdbcTemplate.query(sql, new RowMapper<Prevalidacion>() {
            @Override
            public Prevalidacion mapRow(ResultSet rs, int rowNum) throws SQLException {
                Prevalidacion prevalidacion = new Prevalidacion();
                prevalidacion.setId(rs.getInt(ID));
                prevalidacion.setIdTipoTramite(idTipoTramite);
                prevalidacion.setCuit(rs.getString(CUIT));

                return prevalidacion;
            }

        });

        return listPrevalidacion;
    }

    protected int getMaxId(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = "SELECT MAX(ID) as maxID FROM " + DBTables.TAD_PREVALIDACION;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {

            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getInt("maxID");
                }
                return 0;
            }
        });
    }
}
