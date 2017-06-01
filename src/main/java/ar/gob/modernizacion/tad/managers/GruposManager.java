package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by MMargonari on 01/06/2017.
 */
public class GruposManager {
    private static String ID="ID";
    private static String DESCRIPCION="DESCRIPCION";

    private static String ID_GRUPO_DOCUMENTO="ID_GRUPO_DOCUMENTO";
    private static String ID_TIPO_DOCUMENTO="ID_TIPO_DOCUMENTO";
    private static String OBLIGATORIO="OBLIGATORIO";
    private static String ORDEN="ORDEN";

    public static void loadGrupos() throws SQLException {
        Connection connection = ConnectionManager.connect();

        String query = "select " + ID+","+DESCRIPCION + " from " + DBTables.TAD_GRUPO_DOCUMENTO;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int id = 0; String descripcion="";
            while (rs.next()) {
                id = rs.getInt(ID);
                descripcion = rs.getString(DESCRIPCION);

                Application.grupos.add(new Grupo(id,descripcion));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);
    }

    public static void asociarDocumentos(int idGrupo, ArrayList<Integer> idDocumentos, byte obligatorio, byte orden) throws SQLException {
        Connection connection = ConnectionManager.connect();
        int id = 0;

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO;
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

        for (int docId: idDocumentos) {
            String insertQuery = "INSERT INTO " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO +
                    "(" + ID + "," + ID_GRUPO_DOCUMENTO + "," + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," +
                    ORDEN + ") "
                    + "VALUES" +
                    "(" + Integer.toString(id) + "," + Integer.toString(idGrupo) + "," + Integer.toString(docId) + ","
                    + obligatorio + "," + orden + ")";
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


