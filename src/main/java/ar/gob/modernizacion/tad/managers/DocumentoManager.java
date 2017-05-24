package ar.gob.modernizacion.tad.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static void insertDocumento(Connection connection,
                                       String acronimo_gedo,
                                       String acronimo_tad,
                                       String nombre,
                                       String descripcion,
                                       String es_embebido) throws SQLException {
        int nextID = 0;

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

        String insertQuery = "INSERT INTO TAD2_GED.TAD_TIPO_DOCUMENTO" +
                "("+ID+","+ACRONIMO_GEDO+","+ACRONIMO_TAD+","+NOMBRE+","+DESCRIPCION+","+ES_EMBEBIDO+") " +
                "VALUES" +
                "("+Integer.toString(nextID)+","+formatSQLString(acronimo_gedo)+","+formatSQLString(acronimo_tad)+","+
                formatSQLString(nombre)+","+formatSQLString(descripcion)+","+es_embebido+")";

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
