package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Tag;

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

    public static List<String> getCategorias() throws SQLException {
        Connection connection = ConnectionManager.connect();
        List<String> categorias = new ArrayList<>();

        String query = "select " + NOMBRE_CATEGORIA + " from TAD2_GED.TAD_ETIQUETA_CATEGORIA";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                categorias.add(rs.getString(NOMBRE_CATEGORIA));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);
        return categorias;
    }

    public static void loadEtiquetas() throws SQLException {
        Connection connection = ConnectionManager.connect();
        List<String> etiquetasString = new ArrayList<>();

        String query = "select " + ETIQUETA_CONFIGURACION + " from TAD2_GED.TAD_ETIQUETA";
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

        for (String etiquetaString: etiquetasString) {
            int tagBegin = etiquetaString.indexOf(":") + 1;
            int tagEnd = etiquetaString.indexOf("categorias") - 2;
            String etiqueta = etiquetaString.substring(tagBegin, tagEnd);

            int catBegin = etiquetaString.indexOf("[") + 1;
            int catEnd = etiquetaString.indexOf("]");
            String categoria = etiquetaString.substring(catBegin, catEnd);

            Application.etiquetas.get(categoria).add(new Tag(etiqueta,categoria));
        }

        ConnectionManager.disconnect(connection);
    }
}