package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by martinm on 24/05/17.
 */
public class DocumentoManager {

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

    private static boolean LOADED = false;

    public static void loadDocumentos() throws SQLException {
        if (LOADED) return;

        Connection connection = ConnectionManager.connect();

        String query = "SELECT " +
                ID+","+DESCRIPCION+","+ACRONIMO_GEDO+","+ACRONIMO_TAD+","+NOMBRE+","+
                DESCRIPCION+","+USUARIO_CREACION+","+FECHA_ALTA+","+ES_EMBEBIDO+","+
                FIRMA_CON_TOKEN+","+ES_FIRMA_CONJUNTA+" "
                + "FROM " + DBTables.TAD_TIPO_DOCUMENTO
                + " ORDER BY "+ ID;

        Statement stmt = null;
        Documento documento = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                documento = new Documento(
                        rs.getInt(ID),
                        rs.getString(ACRONIMO_GEDO),
                        rs.getString(ACRONIMO_TAD),
                        rs.getString(NOMBRE),
                        rs.getString(DESCRIPCION),
                        rs.getByte(ES_EMBEBIDO),
                        rs.getByte(FIRMA_CON_TOKEN),
                        rs.getByte(ES_FIRMA_CONJUNTA),
                        rs.getString(USUARIO_CREACION));
                Application.documentos.put(documento.getId(),documento);
                Application.acronimosTads += rs.getString(ACRONIMO_TAD) + ",";
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

        LOADED = true;
    }

    public static void insertDocumento(Documento documento) throws SQLException {
        int nextID = 0;

        Connection connection = ConnectionManager.connect();

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_TIPO_DOCUMENTO;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                documento.setId(rs.getInt(1) + 1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaAlta = formatDate.format(date).toUpperCase();

        String insertQuery = "INSERT INTO " + DBTables.TAD_TIPO_DOCUMENTO +
                "("+ID+","+ACRONIMO_GEDO+","+ACRONIMO_TAD+","+NOMBRE+","+DESCRIPCION+","+USUARIO_CREACION+","+
                FECHA_ALTA+","+ES_EMBEBIDO+","+FIRMA_CON_TOKEN+","+ES_FIRMA_CONJUNTA+") " +
                "VALUES" +
                "("+Integer.toString(documento.getId())+","+formatSQLString(documento.getAcronimoGedo())+","+
                formatSQLString(documento.getAcronimoTad())+","+formatSQLString(documento.getNombre())+","+
                formatSQLString(documento.getDescripcion())+","+formatSQLString(documento.getUsuarioCreacion())+","+
                formatSQLString(fechaAlta)+","+documento.getEsEmbebido()+","+documento.getFirmaConToken()+
                ","+documento.getEsFirmaConjunta()+")";

        Statement insertStatement = null;

        System.out.println(insertQuery);

        try {
            insertStatement = connection.createStatement();
            insertStatement.executeUpdate(insertQuery);
            insertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (insertStatement != null)
                insertStatement.close();
        }

        Application.documentos.put(documento.getId(),documento);

        ConnectionManager.disconnect(connection);
    }

    public static void updateDocumento(Documento documento, String usuario) throws SQLException {

        Connection connection = ConnectionManager.connect();

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaModificacion = formatDate.format(date).toUpperCase();


        String updateQuery = "UPDATE " + DBTables.TAD_TIPO_DOCUMENTO + " SET " +
                ACRONIMO_GEDO+"="+formatSQLString(documento.getAcronimoGedo())+","+
                ACRONIMO_TAD+"="+formatSQLString(documento.getAcronimoTad())+","+
                NOMBRE+"="+formatSQLString(documento.getNombre())+","+
                DESCRIPCION+"="+formatSQLString(documento.getDescripcion())+","+
                USUARIO_MODIFICACION+"="+formatSQLString(usuario)+","+
                FECHA_MODIFICACION+"="+formatSQLString(fechaModificacion)+","+
                ES_EMBEBIDO+"="+documento.getEsEmbebido()+","+
                FIRMA_CON_TOKEN+"="+documento.getFirmaConToken()+","+
                ES_FIRMA_CONJUNTA+"="+documento.getEsFirmaConjunta()+" "
                +"WHERE "+ ID + " = " + Integer.toString(documento.getId());

        Statement updateStatement = null;

        System.out.println(updateQuery);

        try {
            updateStatement = connection.createStatement();
            updateStatement.executeUpdate(updateQuery);
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }

        ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}
