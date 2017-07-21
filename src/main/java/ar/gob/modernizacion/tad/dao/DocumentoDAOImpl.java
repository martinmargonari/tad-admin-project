package ar.gob.modernizacion.tad.dao;


import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.User;
import ar.gob.modernizacion.tad.model.constants.DBTables;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 21/07/2017.
 */

@Repository
public class DocumentoDAOImpl extends GeneralDAO implements DocumentoDAO {

    private static String ID="ID";
    private static String ACRONIMO_GEDO="ACRONIMO_GEDO";
    private static String ACRONIMO_TAD="ACRONIMO_TAD";
    private static String NOMBRE="NOMBRE";
    private static String DESCRIPCION="DESCRIPCION";
    private static String ES_EMBEBIDO="ES_EMBEBIDO";
    private static String FIRMA_CON_TOKEN="FIRMA_CON_TOKEN";
    private static String ES_FIRMA_CONJUNTA="ES_FIRMA_CONJUNTA";
    private static String USUARIO_CREACION="USUARIO_CREACION";
    private static String FECHA_ALTA="FECHA_ALTA";
    private static String USUARIO_MODIFICACION="USUARIO_MODIFICACION";
    private static String FECHA_MODIFICACION="FECHA_MODIFICACION";

    private static List<Documento> documentosCache;

    public DocumentoDAOImpl() {
        table = DBTables.TAD_TIPO_DOCUMENTO;
        columns = new ArrayList<>();
        columns.add(ID);
        columns.add(ACRONIMO_GEDO);
        columns.add(ACRONIMO_TAD);
        columns.add(NOMBRE);
        columns.add(DESCRIPCION);
        columns.add(ES_EMBEBIDO);
        columns.add(FIRMA_CON_TOKEN);
        columns.add(ES_FIRMA_CONJUNTA);
        columns.add(USUARIO_CREACION);
        columns.add(FECHA_ALTA);
    }

    @Override
    public Documento get(int documentoId, User user) {
        for(int index = 0; index < documentosCache.size(); index++) {
            if (documentosCache.get(index).getId() == documentoId) {
                return documentosCache.get(index);
            }
        }
        jdbcTemplate = new JdbcTemplate(dataSource(user));
        String sql = getSelectStatement("ID = " + documentoId);
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Documento documento = new Documento();
                executeSelectQuery(documento, rs);
                return documento;
            }
            return null;
        });
    }

    @Override
    public synchronized Documento insert(Documento documento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int id = getMaxId(user) + 1;
        documento.setId(id);
        documento.setUsuarioCreacion(user.getUsername());
        String sql = getInsertStatement();

        jdbcTemplate.update(sql, documento.getId(), documento.getAcronimoGedo(), documento.getAcronimoTad(),
                documento.getNombre(), documento.getDescripcion(), documento.getEsEmbebido(), documento.getFirmaConToken(),
                documento.getEsFirmaConjunta(), documento.getUsuarioCreacion().toUpperCase(), getToday());

        if (documentosCache != null)
            documentosCache.add(documento);

        return documento;
    }

    @Override
    public Documento update(Documento documento, User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        int i = columns.indexOf(USUARIO_CREACION);
        columns.set(i,USUARIO_MODIFICACION);

        int j = columns.indexOf(FECHA_ALTA);
        columns.set(j,FECHA_MODIFICACION);

        documento.setUsuarioCreacion(user.getUsername());

        String sql = getUpdateStatement();

        jdbcTemplate.update(sql, documento.getAcronimoGedo(), documento.getAcronimoTad(),
                documento.getNombre(), documento.getDescripcion(), documento.getEsEmbebido(), documento.getFirmaConToken(),
                documento.getEsFirmaConjunta(), documento.getUsuarioCreacion().toUpperCase(), getToday(), documento.getId());

        columns.set(i,USUARIO_CREACION);
        columns.set(j,FECHA_ALTA);

        for(int index = 0; index < documentosCache.size(); index++) {
            if (documentosCache.get(index).getId() == documento.getId()) {
                documentosCache.set(index, documento);
                break;
            }
        }

        return documento;
    }

    @Override
    public String getAcronimosTad(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        String sql = "SELECT ACRONIMO_TAD FROM " + table;
        List<String> acronimos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString(ACRONIMO_TAD);
        });

        return String.join(",",acronimos);
    }

    @Override
    public List<Documento> list(User user) {
        jdbcTemplate = new JdbcTemplate(dataSource(user));

        if (documentosCache != null)
            return documentosCache;

        String sql = getSelectStatement();
        List<Documento> listDocumento = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Documento documento = new Documento();

            executeSelectQuery(documento, rs);
            return documento;
        });

        documentosCache = listDocumento;

        return documentosCache;
    }

    private void executeSelectQuery(Documento documento, ResultSet rs) throws SQLException {
        documento.setId(rs.getInt(ID));
        documento.setAcronimoGedo(rs.getString(ACRONIMO_GEDO));
        documento.setAcronimoTad(rs.getString(ACRONIMO_TAD));
        documento.setNombre(rs.getString(NOMBRE));
        documento.setDescripcion(rs.getString(DESCRIPCION));
        documento.setEsEmbebido(rs.getByte(ES_EMBEBIDO));
        documento.setFirmaConToken(rs.getByte(FIRMA_CON_TOKEN));
        documento.setEsFirmaConjunta(rs.getByte(ES_FIRMA_CONJUNTA));
        documento.setUsuarioCreacion(rs.getString(USUARIO_CREACION));
    }
}
