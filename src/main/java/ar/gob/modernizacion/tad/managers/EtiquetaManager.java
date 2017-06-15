package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.constants.DBTables;

import javax.validation.constraints.Null;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMargonari on 25/05/2017.
 */
public class EtiquetaManager {

    private static String ID = "ID";
    private static String ETIQUETA_CONFIGURACION = "ETIQUETA_CONFIGURACION";

    private static String ID_CATEGORIA = "ID";
    private static String NOMBRE_CATEGORIA = "NOMBRE";

    private static boolean LOADED_TAGS = false;
    private static boolean LOADED_CATEGORIES = false;

    private static void loadCategorias() throws SQLException {
        if (LOADED_CATEGORIES) return;

        Connection connection = ConnectionManager.connect();

        String query = "select " + NOMBRE_CATEGORIA + " from " + DBTables.TAD_ETIQUETA_CATEGORIA;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Application.etiquetas.put(rs.getString(NOMBRE_CATEGORIA),new ArrayList<>());
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

        LOADED_CATEGORIES = true;
    }

    public static void loadEtiquetas() throws SQLException {
        if (LOADED_TAGS) return;

        loadCategorias();

        Connection connection = ConnectionManager.connect();
        List<String> etiquetasString = new ArrayList<>();

        String query = "select " + ETIQUETA_CONFIGURACION + " from " + DBTables.TAD_ETIQUETA;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                etiquetasString.add(rs.getString(ETIQUETA_CONFIGURACION));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        String regexp = ".\"(.*)\".*\"(.*)\".*\"(.*)\".*\"(.*)\".*";
        int i = 0;
        for (String etiquetaString: etiquetasString) {
            String etiqueta = etiquetaString.replaceAll(regexp, "$2");
            String categoria = etiquetaString.replaceAll(regexp, "$4");
            i++;
            try {
                Application.etiquetas.get(categoria).add(new Tag(etiqueta, categoria));
            } catch (NullPointerException e){
                try {
                    throw new Exception("Error al parsear la linea " + Integer.valueOf(i) + " de la tabla " + DBTables.TAD_ETIQUETA,e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }

        ConnectionManager.disconnect(connection);

        LOADED_TAGS = true;
    }

    public static void insertEtiqueta(Tag tag) throws SQLException {

        Connection connection = ConnectionManager.connect();

        int id = 0;
        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_ETIQUETA;
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

        String labelTag = "\"tag\"";
        String labelCategoria = "\"categorias\"";
        String etiqueta = "\""+tag.getTag()+"\"";
        String categoria = "\""+tag.getCategoria()+"\"";
        String etiquetaInDB = "{"+labelTag+":"+etiqueta+","+labelCategoria+":["+categoria+"]}";

        String insertQuery = "INSERT INTO " + DBTables.TAD_ETIQUETA +
                "("+ID+","+ETIQUETA_CONFIGURACION+")"
                +" VALUES"+
                "(" +Integer.toString(id)+","+formatSQLString(etiquetaInDB)+")";

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

        Application.etiquetas.get(tag.getCategoria()).add(tag);

        ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }

}