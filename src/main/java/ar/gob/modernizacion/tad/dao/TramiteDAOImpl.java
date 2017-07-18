package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.constants.DBTables;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 30/06/2017.
 */

@Repository
public class TramiteDAOImpl extends GeneralDAO implements TramiteDAO {

    private static String ID="ID";
    private static String DESCRIPCION="DESCRIPCION";
    private static String ID_TRAMITE_CONFIGURACION="ID_TRAMITE_CONFIGURACION";
    private static String USUARIO_CREACION="USUARIO_CREACION";
    private static String TRATA_EE="TRATA_EE";
    private static String USUARIO_INICIADOR_EE="USUARIO_INICIADOR_EE";
    private static String REPARTICION_INICIADORA_EE="REPARTICION_INICIADORA_EE";
    private static String SECTOR_INICIADOR_EE="SECTOR_INICIADOR_EE";
    private static String NOMBRE="NOMBRE";
    private static String ETIQUETAS="ETIQUETAS";
    private static String PAGO="PAGO";
    private static String ID_TIPO_TRAMITE_SIR="ID_TIPO_TRAMITE_SIR";
    private static String DESCRIPCION_HTML="DESCRIPCION_HTML";
    private static String OBLIGATORIO_INTERVINIENTE="OBLIGATORIO_INTERVINIENTE";
    private static String VISIBLE="VISIBLE";
    private static String DESCRIPCION_CORTA="DESCRIPCION_CORTA";
    private static String PREVALIDACION="PREVALIDACION";

    public TramiteDAOImpl() {
        table = DBTables.TAD_TIPO_TRAMITE;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(DESCRIPCION);
        columns.add(ID_TRAMITE_CONFIGURACION);
        columns.add(USUARIO_CREACION);
        columns.add(TRATA_EE);
        columns.add(USUARIO_INICIADOR_EE);
        columns.add(REPARTICION_INICIADORA_EE);
        columns.add(SECTOR_INICIADOR_EE);
        columns.add(NOMBRE);
        columns.add(ETIQUETAS);
        columns.add(PAGO);
        columns.add(ID_TIPO_TRAMITE_SIR);
        columns.add(DESCRIPCION_HTML);
        columns.add(OBLIGATORIO_INTERVINIENTE);
        columns.add(VISIBLE);
        columns.add(DESCRIPCION_CORTA);
        columns.add(PREVALIDACION);
    }

    @Override
    public Tramite get(int tramiteId, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = getSelectStatement("ID = " + tramiteId);
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Tramite tramite = new Tramite();
                executeSelectQuery(tramite, rs);
                return tramite;
            }
            return null;
        });
    }

    @Override
    public Tramite insert(Tramite tramite, User user) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = "tramites", key = "#user.username")
    public List<Tramite> list(User user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource(user));

        String sql = getSelectStatement();
        List<Tramite> listTramite = jdbcTemplate.query(sql, new RowMapper<Tramite>() {
            @Override
            public Tramite mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tramite tramite = new Tramite();

                executeSelectQuery(tramite, rs);
                return tramite;
            }

        });

        return listTramite;
    }

    private void executeSelectQuery(Tramite tramite, ResultSet rs) throws SQLException{
        tramite.setId(rs.getInt(ID));
        tramite.setDescripcion(rs.getString(DESCRIPCION));
        tramite.setIdTramiteConfiguracion(rs.getByte(ID_TRAMITE_CONFIGURACION));
        tramite.setUsuarioCreacion(rs.getString(USUARIO_CREACION));
        tramite.setTrata(rs.getString(TRATA_EE));
        tramite.setUsuarioIniciador(rs.getString(USUARIO_INICIADOR_EE));
        tramite.setReparticion(rs.getString(REPARTICION_INICIADORA_EE));
        tramite.setSector(rs.getString(SECTOR_INICIADOR_EE));
        tramite.setNombre(rs.getString(NOMBRE));
        tramite.setEtiquetas(rs.getString(ETIQUETAS));
        tramite.setPago(rs.getByte(PAGO));
        tramite.setIdTipoTramiteSir(rs.getString(ID_TIPO_TRAMITE_SIR));
        tramite.setDescripcionHtml(rs.getString(DESCRIPCION_HTML));
        tramite.setObligatorioInterviniente(rs.getByte(OBLIGATORIO_INTERVINIENTE));
        tramite.setVisible(rs.getByte(VISIBLE));
        tramite.setDescripcionCorta(rs.getString(DESCRIPCION_CORTA));
        tramite.setPrevalidacion(rs.getByte(PREVALIDACION));
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
