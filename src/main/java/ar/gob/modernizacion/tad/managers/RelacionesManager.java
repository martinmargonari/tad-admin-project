package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by MMargonari on 01/06/2017.
 */
public class RelacionesManager {
    private static String ID="ID";
    private static String ID_TIPO_TRAMITE="ID_TIPO_TRAMITE";
    private static String ID_TIPO_DOCUMENTO="ID_TIPO_DOCUMENTO";
    private static String OBLIGATORIO="OBLIGATORIO";
    private static String CANTIDAD="CANTIDAD";
    private static String ORDEN="ORDEN";
    private static String USUARIO_CREACION="USUARIO_CREACION";

    public static void relacionar(int tramiteId, ArrayList<Integer> documentosId, byte obligatorio, byte cantidad, byte orden, String usuarioCreacion) throws SQLException {
            Connection connection = ConnectionManager.connect();
            int id = 0;

            String queryMaxID = "select MAX(ID) from " + DBTables.TAD_DOCUMENTO_REQUERIDO;
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(queryMaxID);
                if (rs.next()) {
                    id = rs.getInt(1) + 1;
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally {
                if (stmt != null)
                    stmt.close();
            }

            for (int docId: documentosId) {
                String insertQuery = "INSERT INTO " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                        "(" + ID + "," + ID_TIPO_TRAMITE + "," + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," + CANTIDAD + "," +
                        ORDEN + "," + USUARIO_CREACION + ") "
                        + "VALUES" +
                        "(" + Integer.toString(id) + "," + Integer.toString(tramiteId) + "," + Integer.toString(docId) + ","
                        + obligatorio + "," + cantidad + "," + orden + "," + formatSQLString(usuarioCreacion) + ")";
                System.out.println(insertQuery);

                Statement insertStatement = null;
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

                id++;
            }

            ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}
