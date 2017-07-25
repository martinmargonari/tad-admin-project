package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Grupo;
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
public class GrupoDAOImpl extends GeneralDAO implements GrupoDAO {

    private static final String ID = "ID";
    private static final String DESCRIPCION = "DESCRIPCION";

    private static List<Grupo> gruposCache;

    public GrupoDAOImpl() {
        table = DBTables.TAD_GRUPO_DOCUMENTO;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(DESCRIPCION);
    }

    @Override
    public Grupo get(int grupoId, User user) {
        for(int index = 0; index < gruposCache.size(); index++) {
            if (gruposCache.get(index).getId() == grupoId) {
                return gruposCache.get(index);
            }
        }
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = getSelectStatement("ID = " + grupoId);
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Grupo grupo = new Grupo();
                grupo.setId(rs.getInt(ID));
                grupo.setDescripcion(rs.getString(DESCRIPCION));
                return grupo;
            }
            return null;
        });
    }

    @Override
    public synchronized Grupo insert(Grupo grupo, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        grupo.setId(id);
        String sql = getInsertStatement();

        jdbcTemplate.update(sql, grupo.getId(), grupo.getDescripcion());

        if (gruposCache != null)
            gruposCache.add(grupo);

        return grupo;
    }

    @Override
    public Grupo update(Grupo grupo, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        

        String sql = getUpdateStatement();

        jdbcTemplate.update(sql, grupo.getDescripcion(), grupo.getId());
        
        for(int index = 0; index < gruposCache.size(); index++) {
            if (gruposCache.get(index).getId() == grupo.getId()) {
                gruposCache.set(index, grupo);
                break;
            }
        }

        return grupo;
    }

    @Override
    public List<Grupo> list(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        if (gruposCache != null)
            return gruposCache;

        String sql = getSelectStatement();
        List<Grupo> listGrupo = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Grupo grupo = new Grupo();
            grupo.setId(rs.getInt(ID));
            grupo.setDescripcion(rs.getString(DESCRIPCION));
            return grupo;
        });

        gruposCache = listGrupo;

        return gruposCache;
    }
}
