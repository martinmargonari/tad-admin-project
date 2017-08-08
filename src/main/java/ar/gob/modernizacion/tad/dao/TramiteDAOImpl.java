package ar.gob.modernizacion.tad.dao;

import ar.gob.modernizacion.tad.model.constants.DBTables;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 30/06/2017.
 */

@Repository
public class TramiteDAOImpl extends GeneralDAO implements TramiteDAO {

    private static final String ID="ID";
    private static final String DESCRIPCION="DESCRIPCION";
    private static final String ID_TRAMITE_CONFIGURACION="ID_TRAMITE_CONFIGURACION";
    private static final String ID_TRAMITE_TEMPLATE="ID_TRAMITE_TEMPLATE";
    private static final String USUARIO_CREACION="USUARIO_CREACION";
    private static final String FECHA_ALTA="FECHA_ALTA";
    private static final String TRATA_EE="TRATA_EE";
    private static final String USUARIO_INICIADOR_EE="USUARIO_INICIADOR_EE";
    private static final String REPARTICION_INICIADORA_EE="REPARTICION_INICIADORA_EE";
    private static final String SECTOR_INICIADOR_EE="SECTOR_INICIADOR_EE";
    private static final String NOMBRE="NOMBRE";
    private static final String ETIQUETAS="ETIQUETAS";
    private static final String PAGO="PAGO";
    private static final String ID_TIPO_TRAMITE_SIR="ID_TIPO_TRAMITE_SIR";
    private static final String DESCRIPCION_HTML="DESCRIPCION_HTML";
    private static final String OBLIGATORIO_INTERVINIENTE="OBLIGATORIO_INTERVINIENTE";
    private static final String ID_ESTADO_INICIAL="ID_ESTADO_INICIAL";
    private static final String VISIBLE="VISIBLE";
    private static final String DESCRIPCION_CORTA="DESCRIPCION_CORTA";
    private static final String PREVALIDACION="PREVALIDACION";

    private static final String USUARIO_MODIFICACION="USUARIO_MODIFICACION";
    private static final String FECHA_MODIFICACION="FECHA_MODIFICACION";

    private static List<Tramite> tramitesCache;


    public TramiteDAOImpl() {
        table = DBTables.TAD_TIPO_TRAMITE;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(DESCRIPCION);
        columns.add(ID_TRAMITE_CONFIGURACION);
        columns.add(ID_TRAMITE_TEMPLATE);
        columns.add(USUARIO_CREACION);
        columns.add(FECHA_ALTA);
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
        columns.add(ID_ESTADO_INICIAL);
        columns.add(VISIBLE);
        columns.add(DESCRIPCION_CORTA);
        columns.add(PREVALIDACION);
    }

    @Override
    public Tramite get(int tramiteId, User user) {
        for(int index = 0; index < tramitesCache.size(); index++) {
            if (tramitesCache.get(index).getId() == tramiteId) {
                return tramitesCache.get(index);
            }
        }
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
    public synchronized Tramite insert(Tramite tramite, User user) throws DataAccessException {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        tramite.setId(id);
        tramite.setUsuarioCreacion(user.getUsername());
        String sql = getInsertStatement();

        jdbcTemplate.update(sql, tramite.getId(), tramite.getDescripcion(), tramite.getIdTramiteConfiguracion(), 1, tramite.getUsuarioCreacion().toUpperCase(),
        this.getToday(), tramite.getTrata(), tramite.getUsuarioIniciador(), tramite.getReparticion(), tramite.getSector(), tramite.getNombre(),
        tramite.getEtiquetas(), tramite.getPago(), tramite.getIdTipoTramiteSir(), tramite.getDescripcionHtml(), tramite.getObligatorioInterviniente(),
        0, tramite.getVisible(), tramite.getDescripcionCorta(), tramite.getPrevalidacion());

        if (tramitesCache != null)
            tramitesCache.add(tramite);

        return tramite;
    }

    @Override
    public Tramite update(Tramite tramite, User user) throws DataAccessException {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int i = columns.indexOf(USUARIO_CREACION);
        columns.set(i,USUARIO_MODIFICACION);

        int j = columns.indexOf(FECHA_ALTA);
        columns.set(j,FECHA_MODIFICACION);

        tramite.setUsuarioCreacion(user.getUsername());

        String sql = getUpdateStatement();

        jdbcTemplate.update(sql, tramite.getDescripcion(), tramite.getIdTramiteConfiguracion(), 1, tramite.getUsuarioCreacion().toUpperCase(),
                this.getToday(), tramite.getTrata(), tramite.getUsuarioIniciador(), tramite.getReparticion(), tramite.getSector(), tramite.getNombre(),
                tramite.getEtiquetas(), tramite.getPago(), tramite.getIdTipoTramiteSir(), tramite.getDescripcionHtml(), tramite.getObligatorioInterviniente(),
                0, tramite.getVisible(), tramite.getDescripcionCorta(), tramite.getPrevalidacion(), tramite.getId());

        columns.set(i,USUARIO_CREACION);
        columns.set(j,FECHA_ALTA);

        for(int index = 0; index < tramitesCache.size(); index++) {
            if (tramitesCache.get(index).getId() == tramite.getId()) {
                tramitesCache.set(index, tramite);
                break;
            }
        }

        return tramite;
    }

    @Override
    public String getTratas(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = "SELECT TRATA_EE FROM " + table;
        List<String> tratas = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString(TRATA_EE);
        });

        return String.join(",",tratas);
    }

    @Override
    public List<String> getTratasDisponibles(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = "SELECT DISTINCT CODIGO_TRATA FROM EE_GED.TRATA";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString("CODIGO_TRATA");
        });
    }

    @Override
    public List<Tramite> list(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        if (tramitesCache != null)
            return tramitesCache;

        String sql = getSelectStatement();
        List<Tramite> listTramite = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Tramite tramite = new Tramite();

            executeSelectQuery(tramite, rs);
            return tramite;
        });

        tramitesCache = listTramite;

        return tramitesCache;
    }

    private void executeSelectQuery(Tramite tramite, ResultSet rs) throws SQLException {
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
