package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.model.Documento;

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
    private static String USUARIO_CREACION="USUARIO_CREACION";
    private static String FECHA_ALTA="FECHA_ALTA";

    public static void insertDocumento(Documento documento) throws SQLException {
        int nextID = 0;

        Connection connection = ConnectionManager.connect();

        String queryMaxID = "select MAX(ID) from TAD2_GED.TAD_TIPO_DOCUMENTO";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
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

        String insertQuery = "INSERT INTO TAD2_GED.TAD_TIPO_DOCUMENTO" +
                "("+ID+","+ACRONIMO_GEDO+","+ACRONIMO_TAD+","+NOMBRE+","+DESCRIPCION+","+USUARIO_CREACION+","+
                FECHA_ALTA+","+ES_EMBEBIDO+") " +
                "VALUES" +
                "("+Integer.toString(nextID)+","+formatSQLString(documento.getAcronimoGedo())+","+
                formatSQLString(documento.getAcronimoTad())+","+formatSQLString(documento.getNombre())+","+
                formatSQLString(documento.getDescripcion())+","+formatSQLString(documento.getUsuarioCreacion())+","+
                formatSQLString(fechaAlta)+","+documento.getEs_embebido()+")";

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
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}
