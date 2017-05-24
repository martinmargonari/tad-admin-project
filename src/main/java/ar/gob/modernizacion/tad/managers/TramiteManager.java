package ar.gob.modernizacion.tad.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by martinm on 24/05/17.
 */
public class TramiteManager {

    private static String ID="ID";
    private static String DESCRIPCION="DESCRIPCION";
    private static String ID_TRAMITE_CONFIGURACION="ID_TRAMITE_CONFIGURACION";
    private static String ID_TRAMITE_TEMPLATE="ID_TRAMITE_TEMPLATE";
    private static String USUARIO_CREACION="USUARIO_CREACION";
    private static String TRATA_EE="TRATA_EE";
    private static String USUARIO_INICIADOR_EE="USUARIO_INICIADOR_EE";
    private static String REPARTICION_INICIADORA_EE="REPARTICION_INICIADORA_EE";
    private static String SECTOR_INICIADOR_EE="SECTOR_INICIADOR_EE";
    private static String NOMBRE="NOMBRE";
    private static String ETIQUETAS="ETIQUETAS";
    private static String PAGO="PAGO";
    private static String ID_TIPO_TRAMITE_SIR="ID_TIPO_TRAMITE_SIR";
    private static String DESCRIPCION_HTML="DESCRIPCION_HTML";
    private static String OBLIGATORIO_INTERVINIENTE="OBLIGATORIO_INTERVINIENTE";
    private static String ID_ESTADO_INICIAL="ID_ESTADO_INICIAL";
    private static String VISIBLE="VISIBLE";
    private static String PREVALIDACION="PREVALIDACION";

    public static void insertTramite(Connection connection,
                                     String descripcion,
                                     String id_tramite_configuracion,
                                     String trata,
                                     String usuario,
                                     String reparticion,
                                     String sector,
                                     String nombre,
                                     ArrayList<String> etiquetas,
                                     String pago,
                                     String id_tipo_tramite_sir,
                                     String descripcion_html,
                                     String prevalidacion
                                     ) throws SQLException {

        int nextID = 0;
        String queryMaxID = "select MAX(ID) from TAD2_GED.TAD_TIPO_TRAMITE";
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

        String tags="{\"tags\":[";

        for (String etiqueta:etiquetas) {
            tags=tags+"\""+etiqueta+"\",";
        }

        // Remove last comma
        tags=tags.substring(0,tags.length()-1);

        // Close tags
        tags=tags+"]}";

        String insertQuery = "INSERT INTO TAD2_GED.TAD_TIPO_TRAMITE" +
                "("+ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+") "
                +"VALUES"+
                "(" +Integer.toString(nextID)+","+formatSQLString(descripcion)+","+id_tramite_configuracion+
                ",1,'MIGRACION',"+formatSQLString(trata)+ ","+formatSQLString(usuario)+","+formatSQLString(reparticion)+
                ","+formatSQLString(sector)+","+formatSQLString(nombre)+","+formatSQLString(tags)+","+pago+
                ","+formatSQLString(id_tipo_tramite_sir)+","+formatSQLString(descripcion_html)+",0,0,1)";

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
