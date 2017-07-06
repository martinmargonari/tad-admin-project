package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.User;
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

    /**
     * @param tramiteId id del tramite
     * @param relacionesInsert string format: [ID_DOC,OBLIG,CANT,ORDEN,USER]
     *                   separados por ;
     *                   Por ejemplo: 1291,1,1,1,USER;1310,0,2,5,USER
     */
    public static synchronized void relacionar(int tramiteId, String relacionesInsert, User user) throws SQLException {
            Connection connection = ConnectionManager.connect(user);
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

            String listRelaciones[] = relacionesInsert.split(";");

            for (String relacion: listRelaciones) {
                String parameters[] = relacion.split(",");

                String insertQuery = "INSERT INTO " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                        "(" + ID + "," + ID_TIPO_TRAMITE + "," + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," + CANTIDAD + "," +
                        ORDEN + "," + USUARIO_CREACION + ") "
                        + "VALUES" +
                        "(" + Integer.toString(id) + "," + Integer.toString(tramiteId) + "," + parameters[0] + ","
                        + parameters[1] + "," + parameters[2] + "," + parameters[3] + "," + formatSQLString(parameters[4]) + ")";
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

    public static ArrayList<Integer> getDocumentosRelacionados(int tramiteId, User user) throws SQLException {
        Connection connection = ConnectionManager.connect(user);
        ArrayList<Integer> documentosId = new ArrayList<>();

        String query = "select " + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," + CANTIDAD + "," + ORDEN + "," + USUARIO_CREACION +
                " from " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                " where " + ID_TIPO_TRAMITE + "=" + tramiteId;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                documentosId.add(rs.getInt(ID_TIPO_DOCUMENTO));
                Documento documento = Application.documentos.get(rs.getInt(ID_TIPO_DOCUMENTO));

                documento.setRelacionado((byte)1);
                documento.setRelacion(rs.getByte(OBLIGATORIO),
                                        rs.getByte(CANTIDAD),
                                        rs.getByte(ORDEN),
                                        rs.getString(USUARIO_CREACION));
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

    public static void updateRelaciones(int tramiteId, String docsInsert, User user) throws SQLException {
        Connection connection = ConnectionManager.connect(user);

        String deleteQuery = "DELETE FROM " + DBTables.TAD_DOCUMENTO_REQUERIDO +
                " WHERE " + ID_TIPO_TRAMITE + "=" + Integer.toString(tramiteId);

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

        relacionar(tramiteId, docsInsert, user);

        ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}
