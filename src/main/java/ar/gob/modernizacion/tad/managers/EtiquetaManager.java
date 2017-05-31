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

    private static void loadCategorias() throws SQLException {
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
    }

    public static void loadEtiquetas() throws SQLException {
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

        String regexp = ".\"(.*)\".*(\".*\").*\"(.*)\".*\"(.*)\".*";
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
    }

}