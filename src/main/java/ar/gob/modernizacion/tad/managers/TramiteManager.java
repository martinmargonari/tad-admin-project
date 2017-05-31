package ar.gob.modernizacion.tad.managers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.constants.DBTables;

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
    private static String USUARIO_MODIFICACION="USUARIO_MODIFICACION";
    private static String FECHA_MODIFICACION="FECHA_MODIFICACION";

    public static void loadTramites() throws SQLException {
        Connection connection = ConnectionManager.connect();

        String query = "SELECT " +
                ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+","+PREVALIDACION+" "
                + "FROM " + DBTables.TAD_TIPO_TRAMITE
                + " ORDER BY "+ ID;

        Statement stmt = null;
        Tramite tramite = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                tramite = new Tramite(
                        rs.getInt(ID),
                        rs.getString(DESCRIPCION),
                        rs.getByte(ID_TRAMITE_CONFIGURACION),
                        rs.getString(USUARIO_CREACION),
                        rs.getString(TRATA_EE),
                        rs.getString(USUARIO_INICIADOR_EE),
                        rs.getString(REPARTICION_INICIADORA_EE),
                        rs.getString(SECTOR_INICIADOR_EE),
                        rs.getString(NOMBRE),
                        rs.getString(ETIQUETAS),
                        rs.getByte(PAGO),
                        rs.getString(ID_TIPO_TRAMITE_SIR),
                        rs.getString(DESCRIPCION_HTML),
                        rs.getByte(OBLIGATORIO_INTERVINIENTE),
                        rs.getByte(PREVALIDACION),
                        rs.getByte(VISIBLE));
                Application.tramites.put(tramite.getId(),tramite);
                Application.tratasExistentes += rs.getString(TRATA_EE)+",";
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null)
                stmt.close();
        }

        ConnectionManager.disconnect(connection);

    }

    public static void insertTramite(Tramite tramite) throws SQLException {

        Connection connection = ConnectionManager.connect();

        String queryMaxID = "select MAX(ID) from " + DBTables.TAD_TIPO_TRAMITE;
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


        String insertQuery = "INSERT INTO " + DBTables.TAD_TIPO_TRAMITE +
                "("+ID+","+DESCRIPCION+","+ID_TRAMITE_CONFIGURACION+","+ID_TRAMITE_TEMPLATE+","+USUARIO_CREACION+","+
                FECHA_ALTA+","+TRATA_EE+","+USUARIO_INICIADOR_EE+","+REPARTICION_INICIADORA_EE+","+SECTOR_INICIADOR_EE+","+NOMBRE+","+
                ETIQUETAS+","+PAGO+","+ID_TIPO_TRAMITE_SIR+","+DESCRIPCION_HTML+","+OBLIGATORIO_INTERVINIENTE+","+
                ID_ESTADO_INICIAL+","+VISIBLE+","+PREVALIDACION+") "
                +"VALUES"+
                 "(" +Integer.toString(tramite.getId())+","+formatSQLString(tramite.getDescripcion())+","+tramite.getIdTramiteConfiguracion()+
                ",1,"+formatSQLString(tramite.getUsuarioCreacion())+","+formatSQLString(fechaAlta)+","+formatSQLString(tramite.getTrata())+
                ","+formatSQLString(tramite.getUsuarioIniciador())+","+formatSQLString(tramite.getReparticion())+ ","+
                formatSQLString(tramite.getSector())+","+formatSQLString(tramite.getNombre())+","+
                formatSQLString(tramite.getEtiquetas())+","+tramite.getPago()+","+formatSQLString(tramite.getIdTipoTramiteSir())+","+
                formatSQLString(tramite.getDescripcionHtml())+","+tramite.getObligatorioInterviniente()+",0,1,"+tramite.getPrevalidacion()+")";

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

        Application.tramites.put(tramite.getId(),tramite);

        ConnectionManager.disconnect(connection);
    }

    public static void updateTramite(Tramite tramite, String usuario) throws SQLException {

        Connection connection = ConnectionManager.connect();

        java.util.Date date = new java.util.Date(Calendar.getInstance().getTime().getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yy");
        String fechaModificacion = formatDate.format(date).toUpperCase();


        String updateQuery = "UPDATE " + DBTables.TAD_TIPO_TRAMITE + " SET " +
                DESCRIPCION+"="+formatSQLString(tramite.getDescripcion())+","+
                ID_TRAMITE_CONFIGURACION+"="+tramite.getIdTramiteConfiguracion()+","+
                USUARIO_MODIFICACION+"="+formatSQLString(usuario)+","+
                FECHA_MODIFICACION+"="+formatSQLString(fechaModificacion)+","+
                TRATA_EE+"="+formatSQLString(tramite.getTrata())+","+
                USUARIO_INICIADOR_EE+"="+formatSQLString(tramite.getUsuarioIniciador())+","+
                REPARTICION_INICIADORA_EE+"="+formatSQLString(tramite.getReparticion())+","+
                SECTOR_INICIADOR_EE+"="+formatSQLString(tramite.getSector())+","+
                NOMBRE+"="+formatSQLString(tramite.getNombre())+","+
                ETIQUETAS+"="+formatSQLString(tramite.getEtiquetas())+","+
                PAGO+"="+tramite.getPago()+","+
                ID_TIPO_TRAMITE_SIR+"="+formatSQLString(tramite.getIdTipoTramiteSir())+","+
                DESCRIPCION_HTML+"="+formatSQLString(tramite.getDescripcionHtml())+","+
                OBLIGATORIO_INTERVINIENTE+"="+tramite.getObligatorioInterviniente()+","+
                VISIBLE+"="+tramite.getVisible()+","+
                PREVALIDACION+"="+tramite.getPrevalidacion()+" "
                +"WHERE "+ ID + " = " + Integer.toString(tramite.getId());

        Statement updateStatement = null;

        System.out.println(updateQuery);

        try {
            updateStatement = connection.createStatement();
            updateStatement.executeUpdate(updateQuery);
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }

        ConnectionManager.disconnect(connection);
    }

    private static String formatSQLString(String field) {
        return "'" + field + "'";
    }


}
