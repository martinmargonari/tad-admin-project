package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.*;
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

    public static ArrayList<Integer> getDocumentosRelacionados(int tramiteId) throws SQLException {
        Connection connection = ConnectionManager.connect();
        ArrayList<Integer> documentosId = new ArrayList<>();

        String query = "select " + ID_TIPO_DOCUMENTO +
                " from " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                " where " + ID_TIPO_TRAMITE + "=" + tramiteId;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                documentosId.add(rs.getInt(ID_TIPO_DOCUMENTO));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

        return documentosId;
    }

    public static void updateRelaciones(int tramiteId, ArrayList<Integer> docsInsert, ArrayList<Integer> docsDelete, byte obligatorio, byte cantidad, byte orden, String usuarioCreacion) throws SQLException {
        relacionar(tramiteId,docsInsert,obligatorio,cantidad,orden,usuarioCreacion);

        Connection connection = ConnectionManager.connect();

        for (int docId: docsDelete) {
            String deleteQuery = "DELETE FROM " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                    " WHERE " + ID_TIPO_TRAMITE + "=" + Integer.toString(tramiteId) + 
                    " AND " + ID_TIPO_DOCUMENTO + "=" + Integer.toString(docId);

            Statement deleteStatement = null;
            try {
                deleteStatement = connection.createStatement();
                deleteStatement.executeUpdate(deleteQuery);
                deleteStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (deleteStatement != null)
                    deleteStatement.close();
            }
        }

        ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}
