package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.constants.DBTables;

import java.sql.*;
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

    private static String ID_GRUPO="ID_GRUPO";
    private static String ID_TIPO_TRAMITE="ID_TIPO_TRAMITE";

    private static boolean LOADED = false;

    public static void loadGrupos() throws SQLException {
        if (LOADED) return;
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

                Application.grupos.put(id,new Grupo(id,descripcion));
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

    public static synchronized void addNewGrupo(Grupo grupo) throws SQLException {
        Connection connection = ConnectionManager.connect();

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_GRUPO_DOCUMENTO;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                grupo.setId(rs.getInt(1) + 1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        String insertQuery = "INSERT INTO " + DBTables.TAD_GRUPO_DOCUMENTO +
                "(" + ID + "," + DESCRIPCION + ")" +
                "VALUES" +
                "(" + Integer.toString(grupo.getId()) + "," + formatSQLString(grupo.getDescripcion()) + ")";

        Statement insertStatement = null;
        try {
            insertStatement = connection.createStatement();
            insertStatement.executeUpdate(insertQuery);
            insertStatement.close();
        } catch (SQLException e) {
            if (insertStatement != null)
                insertStatement.close();
            ConnectionManager.disconnect(connection);
            throw new SQLException(e);
        }

        Application.grupos.put(grupo.getId(),grupo);

        ConnectionManager.disconnect(connection);
    }

    public static ArrayList<Integer> getGruposTramite(int tramiteId) throws SQLException {
        Connection connection = ConnectionManager.connect();
        ArrayList<Integer> documentosId = new ArrayList<>();

        String query = "select " + ID_GRUPO +
                " from " + DBTables.TAD_T_TRAMITE_G_DOCUMENTO +
                " where " + ID_TIPO_TRAMITE + "=" + tramiteId;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                documentosId.add(rs.getInt(ID_GRUPO));
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

    /**
     * @param tramiteId id del tramite
     * @param gruposInsert string format: [ID_GRUPO]
     *                   separados por ;
     *                   Por ejemplo: 110;1100;120
     */
    public static void asociarGrupos(int tramiteId, String gruposInsert) throws SQLException {
        Connection connection = ConnectionManager.connect();

        String listGrupos[] = gruposInsert.split(";");

        for (String idGrupo: listGrupos) {
            String insertQuery = "INSERT INTO " + DBTables.TAD_T_TRAMITE_G_DOCUMENTO +
                    "(" +  ID_TIPO_TRAMITE + "," + ID_GRUPO + ") "
                    + "VALUES" +
                    "(" + Integer.toString(tramiteId) + "," + idGrupo + ")";
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
        }

        ConnectionManager.disconnect(connection);

    }


    public static void updateGruposTramite(int tramiteId, String gruposInsert) throws SQLException {
        Connection connection = ConnectionManager.connect();

        String deleteQuery = "DELETE FROM " + DBTables.TAD_T_TRAMITE_G_DOCUMENTO +
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

        ConnectionManager.disconnect(connection);

        asociarGrupos(tramiteId, gruposInsert);
    }

    public static ArrayList<Integer> getDocumentosGrupo(int grupoId) throws SQLException {
        Connection connection = ConnectionManager.connect();
        ArrayList<Integer> documentosId = new ArrayList<>();

        String query = "select " + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," + ORDEN +
                " from " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO +
                " where " + ID_GRUPO_DOCUMENTO + "=" + grupoId;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                documentosId.add(rs.getInt(ID_TIPO_DOCUMENTO));
                Documento documento = Application.documentos.get(rs.getInt(ID_TIPO_DOCUMENTO));

                documento.setRelacionado((byte)1);
                documento.setRelacion(rs.getByte(OBLIGATORIO),
                        (byte)0,
                        rs.getByte(ORDEN),
                        "");
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

    /**
     * @param grupoId id del grupo
     * @param documentosInsert string format: [ID_DOC,OBLIG,ORDEN]
     *                   separados por ;
     *                   Por ejemplo: 1291,1,1;1310,0,2
     */
    public static synchronized void asociarDocumentos(int grupoId, String documentosInsert) throws SQLException {
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

        String listDocumentos[] = documentosInsert.split(";");

        for (String documento: listDocumentos) {
            String parameters[] = documento.split(",");

            String insertQuery = "INSERT INTO " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO +
                    "(" + ID + "," + ID_GRUPO_DOCUMENTO + "," + ID_TIPO_DOCUMENTO + "," + OBLIGATORIO + "," + ORDEN + ") "
                    + "VALUES" +
                    "(" + Integer.toString(id) + "," + Integer.toString(grupoId) + "," + parameters[0] + ","
                    + parameters[1] + "," + parameters[2] + ")";
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

    public static void updateDocumentosGrupo(int grupoId, String docsInsert) throws SQLException {
        Connection connection = ConnectionManager.connect();

        String deleteQuery = "DELETE FROM " + DBTables.TAD_G_DOCUMENTO_T_DOCUMENTO +
                " WHERE " + ID_GRUPO_DOCUMENTO + "=" + Integer.toString(grupoId);

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

        asociarDocumentos(grupoId, docsInsert);

        ConnectionManager.disconnect(connection);
    }



    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }
}


