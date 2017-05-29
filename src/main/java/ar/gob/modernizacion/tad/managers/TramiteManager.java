package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Tramite;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private static String FECHA_ALTA="FECHA_ALTA";

    public static void insertTramite(Tramite tramite) throws SQLException {

        Connection connection = ConnectionManager.connect();

        String queryMaxID = "select MAX(ID) from TAD2_GED.TAD_TIPO_TRAMITE";
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryMaxID);
            if (rs.next()) {
                tramite.setId(rs.getInt(1) + 1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaAlta = formatDate.format(date).toUpperCase();


        String insertQuery = "INSERT INTO TAD2_GED.TAD_TIPO_TRAMITE" +
                "("+ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                FECHA_ALTA+","+TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+") "
                +"VALUES"+
                 "(" +Integer.toString(tramite.getId())+","+formatSQLString(tramite.getDescripcion())+","+tramite.getIdTramiteConfiguracion()+
                ",1,"+formatSQLString(tramite.getUsuarioCreacion())+","+formatSQLString(fechaAlta)+","+formatSQLString(tramite.getTrata())+
                ","+formatSQLString(tramite.getUsuarioIniciador())+","+formatSQLString(tramite.getReparticion())+ ","+
                formatSQLString(tramite.getSector())+","+formatSQLString(tramite.getNombre())+","+
                formatSQLString(tramite.getEtiquetas())+","+tramite.getPago()+","+formatSQLString(tramite.getIdTipoTramiteSir())+","+
                formatSQLString(tramite.getDescripcionHtml())+","+tramite.getObligatorioInterviniente()+",0,1)";

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

        Application.tramites.add(tramite);

        ConnectionManager.disconnect(connection);
    }

    public static void loadTramites() throws SQLException {
        Connection connection = ConnectionManager.connect();

        String query = "SELECT " +
                ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+" "
                + "FROM TAD2_GED.TAD_TIPO_TRAMITE";

        Statement stmt = null;
        Tramite tramite = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String conf = rs.getString(ID_TRAMITE_CONFIGURACION);
                char id_tramite = ' ';
                if (conf != null)
                    id_tramite = conf.charAt(0);
                tramite = new Tramite(
                        rs.getInt(ID),
                        rs.getString(DESCRIPCION),
                        id_tramite,
                        rs.getString(USUARIO_CREACION),
                        rs.getString(TRATA_EE),
                        rs.getString(USUARIO_INICIADOR_EE),
                        rs.getString(REPARTICION_INICIADORA_EE),
                        rs.getString(SECTOR_INICIADOR_EE),
                        rs.getString(NOMBRE),
                        rs.getString(ETIQUETAS),
                        rs.getString(PAGO).charAt(0),
                        rs.getString(ID_TIPO_TRAMITE_SIR),
                        rs.getString(DESCRIPCION_HTML),
                        rs.getString(OBLIGATORIO_INTERVINIENTE).charAt(0),
                        '0',
                        rs.getString(VISIBLE).charAt(0));
                Application.tramites.add(tramite);
                Application.tratasExistentes += rs.getString(TRATA_EE);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }


}
