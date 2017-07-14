package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by MMargonari on 13/07/2017.
 */

@Repository
public class UserDAOImpl extends GeneralDAO implements UserDAO {

    public UserDAOImpl() {}

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
}
