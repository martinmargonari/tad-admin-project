package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MMargonari on 20/07/2017.
 */

@Repository
public class EtiquetaDAOImpl extends GeneralDAO implements EtiquetaDAO {

    private static final String ID = "ID";
    private static final String ETIQUETA_CONFIGURACION = "ETIQUETA_CONFIGURACION";

    private static final String ID_CATEGORIA = "ID";
    private static final String NOMBRE_CATEGORIA = "NOMBRE";

    public EtiquetaDAOImpl() {
        table = DBTables.TAD_ETIQUETA;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(ETIQUETA_CONFIGURACION);
    }

    @Override
    public Tag insert(Tag etiqueta, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        String sql = getInsertStatement();
        String labelTag = "\"tag\"";
        String labelCategoria = "\"categorias\"";
        String etiquetaNombre = "\""+etiqueta.getTag()+"\"";
        String categoria = "\""+etiqueta.getCategoria()+"\"";
        String etiquetaInDB = "{"+labelTag+":"+etiquetaNombre+","+labelCategoria+":["+categoria+"]}";

        jdbcTemplate.update(sql, id, etiquetaInDB);

        return etiqueta;
    }

    @Override
    public HashMap<String, List<Tag>> list(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        HashMap<String,List<Tag>> etiquetas = new HashMap<>();
        String sqlCategorias = "SELECT " + NOMBRE_CATEGORIA + " FROM " + DBTables.TAD_ETIQUETA_CATEGORIA;
        List<String> categorias = jdbcTemplate.query(sqlCategorias, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(NOMBRE_CATEGORIA);
            }
        });

        for(String categoria: categorias) {
            etiquetas.put(categoria, new ArrayList<>());
        }

        String sql = getSelectStatement();
        List<String> etiquetasString = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(ETIQUETA_CONFIGURACION);
            }

        });

        String regexp = ".\"(.*)\".*\"(.*)\".*\"(.*)\".*\"(.*)\".*";
        int i = 0;
        for (String etiquetaString: etiquetasString) {
            String etiqueta = etiquetaString.replaceAll(regexp, "$2");
            String categoria = etiquetaString.replaceAll(regexp, "$4");
            i++;
            try {
                etiquetas.get(categoria).add(new Tag(etiqueta, categoria));
            } catch (NullPointerException e){
                try {
                    throw new Exception("Error al parsear la linea " + Integer.valueOf(i) + " de la tabla " + table, e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }

        return etiquetas;
    }

    @Override
    public HashMap<String, List<Tag>> list(User user, String[] etiquetasSeleccionadas) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        HashMap<String,List<Tag>> etiquetas = new HashMap<>();
        String sqlCategorias = "SELECT " + NOMBRE_CATEGORIA + " FROM " + DBTables.TAD_ETIQUETA_CATEGORIA;
        List<String> categorias = jdbcTemplate.query(sqlCategorias, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(NOMBRE_CATEGORIA);
            }
        });

        for(String categoria: categorias) {
            etiquetas.put(categoria, new ArrayList<>());
        }

        String sql = getSelectStatement();
        List<String> etiquetasString = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(ETIQUETA_CONFIGURACION);
            }

        });

        String regexp = ".\"(.*)\".*\"(.*)\".*\"(.*)\".*\"(.*)\".*";
        int i = 0;
        for (String etiquetaString: etiquetasString) {
            String etiqueta = etiquetaString.replaceAll(regexp, "$2");
            String categoria = etiquetaString.replaceAll(regexp, "$4");
            i++;
            try {
                etiquetas.get(categoria).add(new Tag(etiqueta, categoria));
            } catch (NullPointerException e){
                try {
                    throw new Exception("Error al parsear la linea " + Integer.valueOf(i) + " de la tabla " + table, e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }

        return etiquetas;
    }
}
